package ru.textanalysis.tawt.jmorfsdk;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.loader.*;
import ru.textanalysis.tawt.ms.model.jmorfsdk.DerivativeForm;
import ru.textanalysis.tawt.ms.model.jmorfsdk.InitialForm;
import ru.textanalysis.tawt.ms.model.jmorfsdk.PostfixMorfCharacteristics;
import ru.textanalysis.tawt.ms.model.jmorfsdk.PrefixMorfCharacteristicsChanges;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.textanalysis.tawt.md.MorphologicalDictionaryProperty.MD_VERSION;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.CONTROL_VALUE;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.MS_VERSION;
import static ru.textanalysis.tawt.ms.model.Property.*;
import static ru.textanalysis.tawt.ms.model.Property.PREFIX_OFFSET_INFO;

@Slf4j
final class LoaderFromFileAndBD {

    private final DatabaseStrings databaseStrings = DatabaseFactory.getInstanceDatabaseStrings();
    private final DatabaseLemmas databaseLemmas = DatabaseFactory.getInstanceDatabaseLemmas();
    private final PrefixInfoFile prefixInfoFile = new PrefixInfoFile();
    private final PostfixInfoFile postfixInfoFile = new PostfixInfoFile();

    LoaderFromFileAndBD() {
        deCompress();
    }

    public JMorfSdkImpl getEmptyJMorfSdk() {
        return new JMorfSdkImpl();
    }

    public JMorfSdk load(boolean isLoadInfoToPredictCharacteristics) {
        JMorfSdk jMorfSdk = getEmptyJMorfSdk();
        try (InputStream stream = new FileInputStream(databaseLemmas.getFilePath())) {
            jMorfSdk = loadJMorfSdk(stream);
        } catch (IOException ex) {
            log.warn("Cannot load JMorfSdk. " + ex.getMessage(), ex);
            return getEmptyJMorfSdk();
        }
        if (isLoadInfoToPredictCharacteristics) {
            try (InputStream stream = new FileInputStream(prefixInfoFile.getFilePath())) {
                jMorfSdk = loadPrefixes((JMorfSdkImpl) jMorfSdk, stream);
            } catch (IOException ex) {
                log.warn("Cannot load Prefixes. " + ex.getMessage(), ex);
                return getEmptyJMorfSdk();
            }
            try (InputStream stream = new FileInputStream(postfixInfoFile.getFilePath())) {
                jMorfSdk = loadPostfixes((JMorfSdkImpl) jMorfSdk, stream);
            } catch (IOException ex) {
                log.warn("Cannot load Postfixes. " + ex.getMessage(), ex);
                return getEmptyJMorfSdk();
            }
            ((JMorfSdkImpl) jMorfSdk).setPredictSetting(isLoadInfoToPredictCharacteristics);
        }
        return jMorfSdk;
    }

    private void deCompress() {
		//По умолчанию idea говорит что проверка всегда false, однако, это всегда так, если не забыли поднять версию MS и MD
		// noinspection ConstantValue
		if (!Objects.equals(MS_VERSION, MD_VERSION)) {
			log.warn(
				"""
					The MS ({}) and MD ({}) versions are not the same.
					There is a possibility of errors in reading the morphological forms of words or in the correct textual representation of words.
					""",
				MS_VERSION,
				MD_VERSION
			);
		}
        databaseStrings.decompressDd();
        databaseLemmas.decompressDd();
        prefixInfoFile.decompress();
        postfixInfoFile.decompress();
    }

    private JMorfSdk loadJMorfSdk(InputStream inputStreamHashAndMorfCharacteristics) {
        JMorfSdkImpl jMorfSdk = getEmptyJMorfSdk();
        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamHashAndMorfCharacteristics)) {
            while (inputStream.available() > 0) {
                loadLexeme(jMorfSdk, inputStream);
            }
        } catch (IOException ex) {
            log.warn("Cannot load JMorfSdk. " + ex.getMessage(), ex);
        }

        return jMorfSdk;
    }

    private JMorfSdk loadPrefixes(JMorfSdkImpl jMorfSdk, InputStream inputStreamPrefixes) {
        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamPrefixes)) {
            while (inputStream.available() > 0) {
                loadPrefix(jMorfSdk, inputStream);
            }
        } catch (IOException ex) {
            log.warn("Cannot load Prefixes. " + ex.getMessage(), ex);
        }

        return jMorfSdk;
    }

    private JMorfSdk loadPostfixes(JMorfSdkImpl jMorfSdk, InputStream inputStreamPostfixes) {
        try (BufferedInputStream inputStream = new BufferedInputStream(inputStreamPostfixes)) {
            while (inputStream.available() > 0) {
                loadPostfix(jMorfSdk, inputStream);
            }
        } catch (IOException ex) {
            log.warn("Cannot load Postfixes. " + ex.getMessage(), ex);
        }

        return jMorfSdk;
    }

    private void loadLexeme(JMorfSdkImpl jMorfSdk, BufferedInputStream inputStream) throws IOException {
        int hashCode = getIntFromBytes(inputStream);
        InitialForm initialForm = InitialForm.builder()
            .formKey(getIntFromBytes(inputStream))
            .typeOfSpeech(getTypeOfSpeechFromBytes(inputStream))
            .morfCharacteristics(getMorfCharacteristicsFromBytes(inputStream))
            .link(getLinkFromBytes(inputStream))
            .derivativeForms(loadDerivative(jMorfSdk, inputStream))
            .build();
        jMorfSdk.addForm(hashCode, initialForm);
    }

    private List<DerivativeForm> loadDerivative(JMorfSdkImpl jMorfSdk, InputStream inputStream) {
        int nextHashCode = getIntFromBytes(inputStream);
        List<DerivativeForm> derivativeForms = new ArrayList<>();
        while (nextHashCode != CONTROL_VALUE) {
            DerivativeForm derivativeForm = new DerivativeForm(getIdForm(inputStream), getMorfCharacteristicsFromBytes(inputStream), getLinkFromBytes(inputStream));
            jMorfSdk.addForm(nextHashCode, derivativeForm);
            nextHashCode = getIntFromBytes(inputStream);
            derivativeForms.add(derivativeForm);
        }
        return derivativeForms;
    }

    private void loadPrefix(JMorfSdkImpl jMorfSdk, BufferedInputStream inputStream) throws IOException {
        boolean isFirstRecord = true;
        byte partOfSpeech = 0;
        long addCharacteristics = 0;
        long deleteCharacteristics = 0;
        List<PrefixMorfCharacteristicsChanges> list = new ArrayList<>();
        byte prefixLength = getByteFromBytes(inputStream);
        String prefix = getStringValueFromBytes(inputStream, prefixLength);
        byte nextByte = getByteFromBytes(inputStream);
        while ((nextByte & 0xFF) != 0xFF) {
            if (((byte) (nextByte & ACTION_BITS)) == PREFIX_SET_PART_OF_SPEECH_VALUE) {
                if (!isFirstRecord) {
                    PrefixMorfCharacteristicsChanges characteristicsChanges = new PrefixMorfCharacteristicsChanges(partOfSpeech,
                        addCharacteristics, deleteCharacteristics);
                    list.add(characteristicsChanges);
                    partOfSpeech = 0;
                    addCharacteristics = 0;
                    deleteCharacteristics = 0;
                }
                partOfSpeech = (byte) (nextByte & PREFIX_OFFSET_INFO);
                isFirstRecord = false;
            } else if (((byte) (nextByte & ACTION_BITS)) == PREFIX_ADD_CHARACTERISTIC_VALUE) {
                byte offsetValue = (byte) (nextByte & PREFIX_OFFSET_INFO);
                addCharacteristics = addCharacteristics | (1L << offsetValue);
            } else if (((byte) (nextByte & ACTION_BITS)) == PREFIX_DELETE_CHARACTERISTIC_VALUE) {
                byte offsetValue = (byte) (nextByte & PREFIX_OFFSET_INFO);
                deleteCharacteristics = deleteCharacteristics | (1L << offsetValue);
            }
            nextByte = getByteFromBytes(inputStream);
        }
        if (!isFirstRecord && (addCharacteristics != 0 || deleteCharacteristics != 0)) {
            PrefixMorfCharacteristicsChanges characteristicsChanges = new PrefixMorfCharacteristicsChanges(partOfSpeech,
                addCharacteristics, deleteCharacteristics);
            list.add(characteristicsChanges);
        }
        jMorfSdk.addPrefix(prefix, list);
    }

    private void loadPostfix(JMorfSdkImpl jMorfSdk, BufferedInputStream inputStream) throws IOException {
        List<PostfixMorfCharacteristics> list = new ArrayList<>();
        byte postfixLength = getByteFromBytes(inputStream);
        String postfix = getStringValueFromBytes(inputStream, postfixLength);
        byte nextByte = getByteFromBytes(inputStream);

        while ((nextByte & 0xFF) != 0xFF) {
            byte partOfSpeech = (byte) (nextByte & 0x7F);
            long postfixFormTags;
            long initialFormTags;
            String initialFormPostfix;
            if ((nextByte & 0x80) != 0) {
                initialFormPostfix = postfix;
                postfixFormTags = getMorfCharacteristicsFromBytes(inputStream);
                initialFormTags = postfixFormTags;
            } else {
                byte initialFormPostfixLength = getByteFromBytes(inputStream);
                initialFormPostfix = getStringValueFromBytes(inputStream, initialFormPostfixLength);
                postfixFormTags = getMorfCharacteristicsFromBytes(inputStream);
                initialFormTags = getMorfCharacteristicsFromBytes(inputStream);
            }

            PostfixMorfCharacteristics postfixMorfCharacteristics = new PostfixMorfCharacteristics(initialFormPostfix,
                partOfSpeech, postfixFormTags, initialFormTags);
            list.add(postfixMorfCharacteristics);

            nextByte = getByteFromBytes(inputStream);
        }

        jMorfSdk.addPostfix(postfix, list);
    }

    private byte getByteFromBytes(InputStream inputStream) {
        return (byte) getValueCodeFromBytes(inputStream, 1);
    }

    private int getIdForm(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
    }

    private int getIntFromBytes(InputStream inputStream) {
        return (int) getValueCodeFromBytes(inputStream, 4);
    }

    private byte getTypeOfSpeechFromBytes(InputStream inputStream) throws IOException {
        return (byte) inputStream.read();
    }

    private long getMorfCharacteristicsFromBytes(InputStream inputStream) {
        return getValueCodeFromBytes(inputStream, 8);
    }

    private long getLinkFromBytes(InputStream inputStream) {
        return getValueCodeFromBytes(inputStream, 8);
    }

    private long getValueCodeFromBytes(InputStream inputStream, int countByte) {
        long returnValue = 0;
        try {
            for (int i = 0; i < countByte; i++) {
                long f = 0xFF & inputStream.read();
                long g1 = f << (8 * (countByte - 1 - i));
                returnValue |= g1;
            }
        } catch (IOException ex) {
            String message = String.format("Неожиданное окончание файла, проверти целостность файлов!%s.\n", ex.getMessage());
            log.warn(message, ex);
        }
        return returnValue;
    }

    private String getStringValueFromBytes(InputStream inputStream, byte bytesCount) {
        byte[] stringValue = new byte[bytesCount];
        try {
            for (int i = 0; i < bytesCount; i++) {
                stringValue[i] = (byte) (0xFF & inputStream.read());
            }
        } catch (IOException ex) {
            String message = String.format("Неожиданное окончание файла, проверти целостность файлов!%s.\n", ex.getMessage());
            log.warn(message, ex);
        }
        return new String(stringValue, StandardCharsets.UTF_8);
    }
}
