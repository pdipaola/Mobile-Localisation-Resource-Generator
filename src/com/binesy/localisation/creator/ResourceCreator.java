package com.binesy.localisation.creator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.binesy.localisation.StringResource;

public abstract class ResourceCreator {

	private ArrayList<StringResource> resources;
	private File outputFile;
	private String replacePattern;

	public ResourceCreator(ArrayList<StringResource> resources, File outputFile, String replacePattern) {
		this.resources = resources;
		this.outputFile = outputFile;
		this.replacePattern = replacePattern;
	}

	public void createResources() {
		if (outputFile.exists()) {
			outputFile.delete();
		} else {
			outputFile.mkdirs();
		}

		// Get first item so total locales can be fetched
		StringResource res = resources.get(0);
		String[] locales = res.getLocales();
		int totalLocales = locales.length;

		for (int i = 0; i < totalLocales; i++) {

			String locale = locales[i].trim();
			String data = createResourceString(resources, locale);

			File localeFile = createFile(outputFile.getAbsolutePath() + "\\" + getGeneratedFilePath(locale));
			writeDataToFile(localeFile, data);
		}
	}

	protected void writeDataToFile(File file, String data) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			bw.append(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected File createFile(String path) {
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		}

		file.getParentFile().mkdirs();

		try {
			file.createNewFile();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected String regexReplaceInString(String data) {
		// 1String regexPattern = "\\{([^;]*)\\}";
		Pattern pattern = Pattern.compile(replacePattern);
		Matcher matcher = pattern.matcher(data);
		return matcher.replaceAll(getRegexReplaceString());
	}

	protected String escapeData(String data) {
		data = data.replace("&", "&amp;");
		data = data.replace("\"", "&quot;");
		data = data.replace("'", "\\'");
		data = data.replace("<", "&lt;");
		data = data.replace(">", "&gt;");
		data = data.replace("\n", "\\n");
		
		return data;
	}

	protected abstract String createResourceString(ArrayList<StringResource> resources, String locale);

	protected abstract String sanitiseKeyString(String key);
	
	protected abstract String getRegexReplaceString();

	protected abstract String getStringItemTemplate();

	protected abstract String getAutoGeneratedText();

	protected abstract String getGeneratedFilePath(String locale);
}
