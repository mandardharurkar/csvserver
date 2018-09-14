package com.gslab.poc.csvserver;

import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gslab.poc.csvserver.model.CatURL;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

public class WriteCsvToResponse {

	private static final Logger LOGGER = LoggerFactory.getLogger(WriteCsvToResponse.class);

	public static void writeCatUrls(PrintWriter writer, List<CatURL> cities) {

		try {

			ColumnPositionMappingStrategy mapStrategy = new ColumnPositionMappingStrategy();

			mapStrategy.setType(CatURL.class);
			mapStrategy.generateHeader();

			String[] columns = new String[] { "id", "url", };
			mapStrategy.setColumnMapping(columns);

			StatefulBeanToCsv btcsv = new StatefulBeanToCsvBuilder(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withMappingStrategy(mapStrategy).withSeparator(',').build();

			btcsv.write(cities);

		} catch (CsvException ex) {

			LOGGER.error("Error mapping Bean to CSV", ex);
		}
	}

	public static void writeCatURL(PrintWriter writer, CatURL city) {

		try {

			ColumnPositionMappingStrategy mapStrategy = new ColumnPositionMappingStrategy();

			mapStrategy.setType(CatURL.class);

			String[] columns = new String[] { "id", "url" };
			mapStrategy.setColumnMapping(columns);

			StatefulBeanToCsv btcsv = new StatefulBeanToCsvBuilder(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withMappingStrategy(mapStrategy).withSeparator(',').build();

			btcsv.write(city);

		} catch (CsvException ex) {

			LOGGER.error("Error mapping Bean to CSV", ex);
		}
	}
}
