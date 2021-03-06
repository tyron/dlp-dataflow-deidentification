/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.swarm.tokenization.common;

import com.google.api.services.bigquery.model.TableSchema;
import com.google.privacy.dlp.v2.Value;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import com.google.privacy.dlp.v2.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.google.privacy.dlp.v2.FieldId;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class UtilTest implements Serializable {

	@Rule
	public final transient TestPipeline p = TestPipeline.create();

	@Rule
	public transient TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void testParseBucketName() {
		String sampleString, output;

		sampleString = "gs://name/";
		output = Util.parseBucketName(sampleString);

		assertEquals("name", output);

	}

	@Test
	public void testConvertCsvRowToTableRow() {
		String csvRow = "this,is,a,sentence";
		Table.Row result = Util.convertCsvRowToTableRow(csvRow);

		assertEquals(result.getValuesCount(), 4);
		assertEquals(result.getValues(0).getStringValue(), "this");
		assertEquals(result.getValues(1).getStringValue(), "is");
		assertEquals(result.getValues(2).getStringValue(), "a");
		assertEquals(result.getValues(3).getStringValue(), "sentence");

	}

	@Test
	public void testCheckHeader() {
		String header_input1 = "Column 1";
		String header_input2 = "bucket/object";
		String header_input3 = "'@twitterhandle'";

		assertEquals(Util.checkHeaderName(header_input1), "Column_1");
		assertEquals(Util.checkHeaderName(header_input2), "bucketobject");
		assertEquals(Util.checkHeaderName(header_input3), "twitterhandle");
	}

	@Test
	public void testCreateDLPTable() {
		List<FieldId> output = new ArrayList<FieldId>();
		output.add(FieldId.newBuilder().setName("First").build());
		output.add(FieldId.newBuilder().setName("Second").build());

		List<String> sArray = new ArrayList<String>();
		sArray.add("t1,t2");
		sArray.add("t3,t4");

		ArrayList<ArrayList<Value>> rows = new ArrayList<ArrayList<Value>>();
		for (String s : sArray) {
			ArrayList<Value> row1 = new ArrayList<Value>();
			String[] values = s.split(",");
			for (String value : values) {
				row1.add(Value.newBuilder().setStringValue(value).build());
			}
			rows.add(row1);
		}

		List<Table.Row> tableRows = new ArrayList<Table.Row>();
		for (ArrayList<Value> row1 : rows) {
			Table.Row.Builder test = Table.Row.newBuilder();
			tableRows.add(test.addAllValues(row1).build());
		}

		Table result = Util.createDLPTable(output, sArray);
		assertEquals(result.getHeadersList(), output);
		assertEquals(result.getRowsList(), tableRows);

	}

	@Test
	public void testFindEncryptionType() {
		assertEquals(Util.findEncryptionType(null, "Test", "Test", "Test"), true);
		assertEquals(Util.findEncryptionType(null, null, null, null), false);
	}

	@Test
	public void testGetReader() throws IOException {
		Path firstPath = tmpFolder.newFile("first").toPath();
		int firstSize = 37;
		Files.write(firstPath, new byte[firstSize]);

		ValueProvider<String> testValueProvider = null;
		PCollection<String> br = p.apply(FileIO.match().filepattern(tmpFolder.getRoot().getAbsolutePath() + "/*"))
				.apply(FileIO.readMatches().withCompression(Compression.UNCOMPRESSED))
				.apply(ParDo.of(new DoFn<FileIO.ReadableFile, String>() {
					@ProcessElement
					public void processElement(@Element FileIO.ReadableFile f, OutputReceiver<String> out)
							throws IOException {
						out.output(Util.getReader(false, "object_name", "bucket_name", f, "key_name", testValueProvider)
								.readLine());
					}
				}));
		p.run();
		assertNotNull(br);
	}

	@Test
	public void testGetSchema() {
		List<String> schema = new ArrayList<String>();
		schema.add("Column1");
		schema.add("Column2");

		TableSchema result = Util.getSchema(schema);

		assertEquals(result.getFields().get(0).getName(), "Column1");
		assertEquals(result.getFields().get(1).getName(), "Column2");
		assertEquals(result.getFields().get(0).getType(), "STRING");
		assertEquals(result.getFields().get(1).getType(), "STRING");

	}

}
