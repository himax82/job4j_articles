package ru.job4j.articles.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.model.Article;
import ru.job4j.articles.model.Word;
import ru.job4j.articles.service.generator.ArticleGenerator;
import ru.job4j.articles.store.Store;

import java.lang.ref.SoftReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleArticleService implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleArticleService.class.getSimpleName());

    private final ArticleGenerator articleGenerator;

    public SimpleArticleService(ArticleGenerator articleGenerator) {
        this.articleGenerator = articleGenerator;
    }

    @Override
    public void generate(Store<Word> wordStore, int count, Store<Article> articleStore) {
        LOGGER.info("Геренация статей в количестве {}", count);
        var words = wordStore.findAll();
        for (int i = 0; i < count / 1000; i++) {
            var articles = IntStream.iterate(0, j -> j < 1000, j -> j + 1)
                    .peek(j -> LOGGER.info("Сгенерирована статья № {}", j))
                    .mapToObj((x) -> articleGenerator.generate(words))
                    .map(SoftReference::new)
                    .collect(Collectors.toList());
            articles.stream().map(SoftReference::get).forEach(articleStore::save);
        }
    }
}
