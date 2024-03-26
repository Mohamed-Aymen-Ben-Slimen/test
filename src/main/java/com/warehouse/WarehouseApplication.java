package com.warehouse;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.warehouse.dao.WidgetDAO;
import com.warehouse.dto.WidgetMapper;
import com.warehouse.entity.Widget;
import com.warehouse.resources.WidgetResource;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class WarehouseApplication extends Application<WarehouseConfiguration> {

    private final HibernateBundle<WarehouseConfiguration> hibernateBundle = new HibernateBundle<WarehouseConfiguration>(
            Widget.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(WarehouseConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final SwaggerBundle<WarehouseConfiguration> swaggerBundle = new SwaggerBundle<WarehouseConfiguration>() {
        @Override
        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(WarehouseConfiguration configuration) {
            return configuration.swaggerBundleConfiguration;
        }
    };

    public static void main(final String[] args) throws Exception {
        new WarehouseApplication().run(args);
    }

    @Override
    public String getName() {
        return "Warehouse";
    }

    @Override
    public void initialize(final Bootstrap<WarehouseConfiguration> bootstrap) {
        bootstrap.addBundle(this.hibernateBundle);
        bootstrap.addBundle(this.swaggerBundle);
    }

    @Override
    public void run(final WarehouseConfiguration configuration,
            final Environment environment) {

        Cache<Long, Widget> cache = Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .build();

        // Register Widget resource
        final WidgetMapper widgetMapper = new WidgetMapper();
        final WidgetDAO widgetDAO = new WidgetDAO(this.hibernateBundle.getSessionFactory(), widgetMapper);
        WidgetResource widgetResource = new WidgetResource(widgetDAO, cache);
        environment.jersey().register(widgetResource);
    }

}
