package ru.textanalysis.tawt.jmorfsdk;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.md.ResourcesUtils;
import ru.textanalysis.tawt.ms.model.jmorfsdk.DerivativeForm;
import ru.textanalysis.tawt.ms.model.jmorfsdk.InitialForm;
import template.wrapper.classes.FileHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import static ru.textanalysis.tawt.ms.loader.BDFormString.deCompressDd;
import static ru.textanalysis.tawt.ms.model.Property.CONTROL_VALUE;
import static ru.textanalysis.tawt.ms.model.Property.NAME_HASH_AND_MORF_CHARACTERISTICS;

@Slf4j
final class LoaderFromFileAndBD {

	LoaderFromFileAndBD() {
		deCompressDd();
	}

	public JMorfSdkImpl getEmptyJMorfSdk() {
		return new JMorfSdkImpl();
	}

	public JMorfSdk load(String pathZipFile) {
		ZipInputStream streamHashAndMorfCharacteristic = null;
		InputStream zipFile = null;
		try {
			zipFile = ResourcesUtils.getResource(pathZipFile);
			if (zipFile == null) {
				throw new IOException("Cannot found zip: " + pathZipFile);
			}
			streamHashAndMorfCharacteristic = FileHelper.openZipFile(zipFile, NAME_HASH_AND_MORF_CHARACTERISTICS);
			return loadJMorfSdk(streamHashAndMorfCharacteristic);
		} catch (IOException ex) {
			log.warn("Cannot load JMorfSdk. " + ex.getMessage(), ex);
			return getEmptyJMorfSdk();
		} finally {
			FileHelper.closeFile(streamHashAndMorfCharacteristic);
			FileHelper.closeFile(zipFile);
		}
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

	private void loadLexeme(JMorfSdkImpl jMorfSdk, BufferedInputStream inputStream) throws IOException {
		int hashCode = getIntFromBytes(inputStream);
		InitialForm initialForm = InitialForm.builder()
			.formKey(getIntFromBytes(inputStream))
			.typeOfSpeech(getTypeOfSpeechFromBytes(inputStream))
			.morfCharacteristics(getMorfCharacteristicsFromBytes(inputStream))
			.derivativeForms(loadDerivative(jMorfSdk, inputStream))
			.build();
		jMorfSdk.addForm(hashCode, initialForm);
	}

	private List<DerivativeForm> loadDerivative(JMorfSdkImpl jMorfSdk, InputStream inputStream) {
		int nextHashCode = getIntFromBytes(inputStream);
		List<DerivativeForm> derivativeForms = new ArrayList<>();
		while (nextHashCode != CONTROL_VALUE) {
			DerivativeForm derivativeForm = new DerivativeForm(getIdForm(inputStream), getMorfCharacteristicsFromBytes(inputStream));
			jMorfSdk.addForm(nextHashCode, derivativeForm);
			nextHashCode = getIntFromBytes(inputStream);
			derivativeForms.add(derivativeForm);
		}
		return derivativeForms;
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

	private long getValueCodeFromBytes(InputStream inputStream, int countByte) {
		long returnValue = 0;
		try {
			for (int i = 0; i < countByte; i++) {
				int f = 0xFF & inputStream.read();
				int g1 = f << (8 * (countByte - 1 - i));
				returnValue |= g1;
			}
		} catch (IOException ex) {
			String message = String.format("Неожиданное окончание файла, проверти целостность файлов!%s.\n", ex.getMessage());
			log.warn(message, ex);
		}
		return returnValue;
	}
}
