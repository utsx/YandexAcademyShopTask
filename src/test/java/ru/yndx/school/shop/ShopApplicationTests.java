package ru.yndx.school.shop;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.yndx.school.shop.components.ItemComponent;
import ru.yndx.school.shop.entities.Answer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

@SpringBootTest
@RunWith(SpringRunner.class)
class ShopApplicationTests {


	/*@Autowired
	ItemComponent itemComponent;

	private String getDataFromFileToJSON(String file) throws FileNotFoundException, ParseException {
		Scanner scanner = new Scanner(new FileInputStream(file));
		StringBuilder line = new StringBuilder();
		while (scanner.hasNext())
			line.append(scanner.nextLine()).append("\n");
		return line.toString();
	}

	private String getDataFromFileToLine(String file) throws FileNotFoundException, ParseException {
		Scanner scanner = new Scanner(new FileInputStream(file));
		StringBuilder line = new StringBuilder();
		while (scanner.hasNext())
			line.append(scanner.nextLine());
		return line.toString();
	}

	private ResponseEntity loadDataToDB(String line) throws FileNotFoundException, ParseException {
		return itemComponent.parse(String.valueOf(line));
	}

	@Test
	@Transactional
	public void checkJSONParseTest() throws FileNotFoundException, ParseException {
		ResponseEntity response  = loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		Assertions.assertEquals(response, new ResponseEntity<>(new Answer(200, "OK"), HttpStatus.OK));
	}

	@Test
	@Transactional
	public void checkNodes404() throws FileNotFoundException, ParseException {
		loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		ResponseEntity response = itemComponent.getItemById("1");
		Assertions.assertEquals(response, new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND));
	}

	@Test
	@Transactional
	public void checkNodes200() throws FileNotFoundException, ParseException {
		loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		String expected = getDataFromFileToLine("src/test/java/ru/yndx/school/shop/data/expected.json");
		ResponseEntity response = itemComponent.getItemById("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");
		Assertions.assertEquals(expected, Objects.requireNonNull(response.getBody()).toString());
	}

	@Test
	@Transactional
	public void checkDeleteNodes404() throws FileNotFoundException, ParseException {
		loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		ResponseEntity response = itemComponent.delete("1");
		Assertions.assertEquals(response, new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND));
	}

	@Test
	@Transactional
	public void checkDeleteNodes200Category() throws FileNotFoundException, ParseException {
		loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		ResponseEntity response = itemComponent.delete("1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2");
		Assertions.assertEquals(response, new ResponseEntity<>(new Answer(200, "OK"), HttpStatus.OK));
		String expected = getDataFromFileToLine("src/test/java/ru/yndx/school/shop/data/expectedWithDeleteCategory.json");
		response = itemComponent.getItemById("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");
		Assertions.assertEquals(expected, Objects.requireNonNull(response.getBody()).toString());
	}

	@Test
	@Transactional
	public void checkDeleteNodes200Offer() throws FileNotFoundException, ParseException {
		loadDataToDB(getDataFromFileToJSON("src/test/java/ru/yndx/school/shop/data/data.json"));
		ResponseEntity response = itemComponent.delete("b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4");
		Assertions.assertEquals(response, new ResponseEntity<>(new Answer(200, "OK"), HttpStatus.OK));
		String expected = getDataFromFileToLine("src/test/java/ru/yndx/school/shop/data/expectedWithDeleteOffer.json");
		response = itemComponent.getItemById("069cb8d7-bbdd-47d3-ad8f-82ef4c269df1");
		Assertions.assertEquals(expected, Objects.requireNonNull(response.getBody()).toString());
	}
*/




}
