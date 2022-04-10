package io.axoniq.foodordering;

import com.fasterxml.jackson.module.kotlin.KotlinFeature;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.eventstore.jdbc.EventTableFactory;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.MySqlEventTableFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.jdbc.SpringDataSourceConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@SpringBootApplication
public class FoodOrderingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingApplication.class, args);
    }
}



@Configuration
class AxonConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public ConnectionProvider dataSourceConnectionProvider() {
        return new SpringDataSourceConnectionProvider(new TransactionAwareDataSourceProxy(this.dataSource));
    }

    @Bean
    public EventTableFactory eventTableFactory(){
        return new MySqlEventTableFactory();
    }

    @Bean
    public JdbcEventStorageEngine jdbcEventStorageEngine(ConnectionProvider connectionProvider, TransactionManager transactionManager) {
        return JdbcEventStorageEngine.builder()
                .connectionProvider(connectionProvider)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @Primary
    public Serializer defaultSerializer() {
        JacksonSerializer jacksonSerializer =  JacksonSerializer.defaultSerializer();
        jacksonSerializer.getObjectMapper().registerModule(new KotlinModule.Builder()
                .build());
        return jacksonSerializer;
    }

    @Autowired
    public void createJdbcEventStorageSchema(JdbcEventStorageEngine jdbcStorageEngine,
                                             EventTableFactory eventTableFactory) {
        jdbcStorageEngine.createSchema(eventTableFactory);
    }
}
