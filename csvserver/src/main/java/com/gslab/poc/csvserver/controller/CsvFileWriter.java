package com.gslab.poc.csvserver.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.gslab.poc.csvserver.model.CatURL;

public class CsvFileWriter {
	/*
	 * public CsvPreference customCsvPreference(){ return new
	 * CsvPreference.Builder('|', ',', "\n").build(); }
	 */

	public static List<CatURL> readCsv(String csvFileName) throws FileNotFoundException, IOException {
		List<CatURL> catURLs = null;

		try (ICsvListReader listReader = new CsvListReader(new FileReader(csvFileName),
				CsvPreference.STANDARD_PREFERENCE)) {
			// First Column is header names- though we don't need it in runtime
			@SuppressWarnings("unused")
			final String[] headers = listReader.getHeader(true);
			CellProcessor[] processors = null;

			List<String> fieldsInCurrentRow;
			while ((fieldsInCurrentRow = listReader.read()) != null) {
				switch (fieldsInCurrentRow.size()) {
				case 10:
					processors = getTenColumnProcessors();
					break;
				case 9:
					processors = getNineColumnProcessors();
					break;
				case 8:
					processors = getEightColumnProcessors();
					break;
				case 7:
					processors = getSevenColumnProcessors();
					break;
				case 6:
					processors = getSixColumnProcessors();
					break;
				case 5:
					processors = getFiveColumnProcessors();
					break;
				case 4:
					processors = getFourColumnProcessors();
					break;
				case 3:
					processors = getThreeColumnProcessors();
					break;
				case 2:
					processors = getTwoColumnProcessors();
					break;
				case 1:
					processors = getOneColumnProcessors();
					break;
				}
				final List<Object> formattedFields = listReader.executeProcessors(processors);
				String url = String.valueOf(formattedFields.get(0));
				
				String cat = StringUtils.join(formattedFields.subList(1, formattedFields.size()),",");

				if (catURLs == null) {
					catURLs = new ArrayList<>();
				}
				catURLs.add(new CatURL(cat, url));
			}
		}

		return catURLs;
	}

	public static void clearCsv(String csvFileName) throws Exception {
        FileWriter fw = new FileWriter(csvFileName); 
        PrintWriter pw = new PrintWriter(fw);
        pw.flush();
        pw.close();
        fw.close();
    }
	
	public static void writeCsv(String csvFileName, List<CatURL> catURLs) throws IOException {
		ICsvBeanWriter beanWriter = null;
		try {

			beanWriter = new CsvBeanWriter(new FileWriter(csvFileName, true), CsvPreference.STANDARD_PREFERENCE);

			final String[] header = new String[] { "URL", "CategoryID" };
			final CellProcessor[] processors = getProcessors();

			boolean writeHeader = true;
			try (ICsvMapReader mapReader = new CsvMapReader(new FileReader(csvFileName),
					CsvPreference.STANDARD_PREFERENCE)) {
				final String[] existingHeader = mapReader.getHeader(true);
				if (Arrays.equals(existingHeader, header)) {
					writeHeader = false;
				}
			} catch (FileNotFoundException ex) {

			}

			if (writeHeader) {
				beanWriter.writeHeader(header);
			}
			// write the beans
			for (final CatURL catURL : catURLs) {
				beanWriter.write(catURL, header, processors);
			}

			// the header elements are used to map the bean values to each
			// column (names must match)

		} finally {
			if (beanWriter != null) {
				beanWriter.close();
			}

		}
	}

	private static CellProcessor[] getProcessors() {
		return new CellProcessor[] { new Optional(), new Optional() };
	}

	private static CellProcessor[] getTenColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional() };
	}

	private static CellProcessor[] getNineColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional() };
	}

	private static CellProcessor[] getEightColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional() };
	}

	private static CellProcessor[] getSevenColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional() };
	}

	private static CellProcessor[] getSixColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional() };
	}

	private static CellProcessor[] getFiveColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional(), new Optional() };
	}

	private static CellProcessor[] getFourColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional(), new Optional() };
	}

	private static CellProcessor[] getThreeColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional(), new Optional() };
	}

	private static CellProcessor[] getTwoColumnProcessors() {
		return new CellProcessor[] { new Optional(), new Optional() };
	}

	private static CellProcessor[] getOneColumnProcessors() {
		return new CellProcessor[] { new Optional() };
	}

	public static void main(String[] args) throws Exception {

		
		CsvFileWriter.clearCsv("C:\\gslab\\poc\\dir\\client1\\URLCategoryClient.csv");

	
	}
}