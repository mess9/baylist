package org.baylist.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProbeController {

    ChatModel chatModel;
    VectorStore vectorStore;


    @GetMapping("/test")
    public String test() {
        return "make love not war";
    }

    @GetMapping("/ai")
    public String ai(@RequestParam String query) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();

        String content = chatClient.prompt("""
                        сформулируй запрос в векторную бд в формате json согласно полученному сообщению
                        """)
                .user(query)
                .call().content();

        System.out.println(content);

        SearchRequest sr = SearchRequest.from(SearchRequest.builder().query(content).build()).build();
        List<Document> documents = vectorStore.similaritySearch(sr);

        assert documents != null;
        return chatClient.prompt("тебе на вход даны результаты поиска в векторной бд " + documents.stream().map(Document::getText).toList())
                .user(query)
                .call().content();
    }

}
