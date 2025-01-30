package org.baylist.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.db.entity.History;
import org.baylist.db.repo.HistoryRepository;
import org.baylist.service.DictionaryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.baylist.dto.Constants.FIL_USER_ID;
import static org.baylist.util.convert.ToJson.getObjectMapper;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MyVectorStore {

	VectorStore vectorStore;
	DictionaryService dictionaryService;
	HistoryRepository historyRepository;

	@EventListener
	public void loadExampleDataToVectorStore(ApplicationStartedEvent event) throws IOException {
//		storeToFile(exampleData());
//		storeVectorDbToFile(dictFromDb());
		storeVectorDbToFile(getHistory());
	}

	private void storeVectorDbToFile(List<Document> data) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		Path tempPath = Path.of(tempDir);

		File jsonFile;

		try (Stream<Path> files = Files.list(tempPath)) {
			Optional<Path> file = files
					.filter(p -> p.getFileName().toString().matches("vectorstore.*\\.json"))
					.findFirst();

			jsonFile = file.map(Path::toFile)
					.orElse(null);
		}

		if (jsonFile != null) {
			((SimpleVectorStore) this.vectorStore).load(jsonFile);
			log.info("vector store loaded from existing vectorstore.json file from temp files");
			return;
		} else {
			vectorStore.add(data);

			if (vectorStore instanceof SimpleVectorStore) {
				var file = File.createTempFile("vectorstore", ".json");
				((SimpleVectorStore) this.vectorStore).save(file);
				log.info("vector store contents written to {}", file.getAbsolutePath());
			}
		}
		log.info("vector store loaded with {} documents", data.size());
	}

	@NotNull
	private static List<Document> exampleData() {
		String json = """
				{
				  "galaxy": "Зонтар-5",
				  "system": "Флуксиан",
				  "species": "Арксиоды",
				  "language": {
				    "name": "Флюксион",
				    "structure": "Каждое слово состоит из трёх корней и передаёт сразу несколько смыслов.",
				    "examples": [
				      {
				        "word": "трелфазим",
				        "meanings": ["движение", "энергия", "расширение пространства"]
				      }
				    ]
				  }
				}
				""";
		byte[] bytes = json.getBytes();
		DocumentReader reader = new JsonReader(new ByteArrayResource(bytes));
		return reader.get();
	}

	private List<Document> dictFromDb() throws JsonProcessingException {
		Map<String, Set<String>> dict = dictionaryService.getDict(FIL_USER_ID);
		ObjectMapper objectMapper = getObjectMapper();
		String s = objectMapper.writeValueAsString(dict);
		return new JsonReader(new ByteArrayResource(s.getBytes())).get();
	}

	private List<Document> getHistory() throws JsonProcessingException {
		List<History> history = historyRepository.findAll();
		ObjectMapper objectMapper = getObjectMapper();
		String s = objectMapper.writeValueAsString(history);
		return new JsonReader(new ByteArrayResource(s.getBytes())).get();
	}

}
