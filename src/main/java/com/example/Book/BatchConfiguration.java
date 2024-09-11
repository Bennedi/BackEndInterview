package com.example.Book;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.support.DatabaseType;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@EnableBatchProcessing

public class BatchConfiguration {
    @Bean
    public FlatFileItemReader<Book> reader() {
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookItemReader")
                .resource(new ClassPathResource("books.csv"))
                .delimited()
                .names("title","author","publishedDated","designation")
                .targetType(Book.class)
                .build();
    }
    @Bean
    public SqlPagingQueryProviderFactoryBean queryFactory() {
        var query = new SqlPagingQueryProviderFactoryBean();
        query.setSelectClause("title,author,publishedDated,designation");
        query.setFromClause("from Book");
        query.setDataSource(dataSource());
        query.setDatabaseType(DatabaseType.H2.name());
        query.setSortKey(Collections.singletonMap("id", Order.ASCENDING).toString());
        return query;
    }

    @Bean
    public BookProcessor bookProcessor() {
        return new BookProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Book> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Book>()
                .sql("INSERT INTO books (id,title,author,published_dated,designation) VALUES(:id,:title,:author,:published_dated,:designation")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
    @Bean
    public Job importBookJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener){
        return new JobBuilder("importBookJob",jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }
    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Book> reader, BookProcessor bookProcessor, JdbcBatchItemWriter<Book> writer) {
        return new StepBuilder("importBookStep1",jobRepository)
                .<Book, Book> chunk(3,transactionManager)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .password("")
                .build();
    }


}
