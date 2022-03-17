package com.gys.common.kylin;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.gys.common.kylin.MapperPlugin.*;

public class RowMapper<T> extends CommonBeanPropertyRowMapper<T> {

    private List<MapperPlugin> mapperPlugins;

    private RowMapper(Class<T> tClass, List<MapperPlugin> mapperPlugins) throws Exception {
        super(tClass);
        this.mapperPlugins = mapperPlugins;
    }

    @Override
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd)
            throws SQLException {
        Object object = rs.getObject(index);
        return mapperPlugins.stream()
                .filter(mapperPlugin -> mapperPlugin.test(pd))
                .map(mapperPlugin -> mapperPlugin.getColumnValue(object, pd))
                .findFirst()
                .orElse(super.getColumnValue(rs, index, pd));
    }

    public static <T> RowMapper<T> getDefault(Class<T> tClass) {
        return RowMapper.<T>builder().tClass(tClass)
                .mapperPlugins(JSONObjectPlugin)
                .mapperPlugins(ListPlugin)
                .mapperPlugins(SetPlugin)
                .mapperPlugins(MapPlugin)
                .mapperPlugins(EnumPlugin)
                .mapperPlugins(JsonPlugin)
                .build();
    }

    public static <T> RowMapper<T> withDefault(Class<T> tClass, MapperPlugin... mapperPlugins) {
        RhllorRowMapperBuilder<T> builder = RowMapper.<T>builder().tClass(tClass);
        for (final MapperPlugin mapperPlugin : mapperPlugins) {
            builder.mapperPlugins(mapperPlugin);
        }
        return builder
                .mapperPlugins(JSONObjectPlugin)
                .mapperPlugins(ListPlugin)
                .mapperPlugins(SetPlugin)
                .mapperPlugins(MapPlugin)
                .mapperPlugins(EnumPlugin)
                .mapperPlugins(JsonPlugin)
                .build();
    }

    public static <T> RowMapper.RhllorRowMapperBuilder<T> builder() {
        return new RowMapper.RhllorRowMapperBuilder<>();
    }

    public static class RhllorRowMapperBuilder<T> {

        private Class<T> tClass;
        private ArrayList<MapperPlugin> mapperPlugins;

        RhllorRowMapperBuilder() {
        }

        public RowMapper.RhllorRowMapperBuilder<T> tClass(Class<T> tClass) {
            this.tClass = tClass;
            return this;
        }

        public RowMapper.RhllorRowMapperBuilder<T> mapperPlugins(MapperPlugin mapperPlugin) {
            if (this.mapperPlugins == null) {
                this.mapperPlugins = new ArrayList();
            }
            this.mapperPlugins.add(mapperPlugin);
            return this;
        }

        public RowMapper<T> build() {
            List<MapperPlugin> mapperPlugins;
            switch (this.mapperPlugins == null ? 0 : this.mapperPlugins.size()) {
                case 0:
                    mapperPlugins = Collections.emptyList();
                    break;
                case 1:
                    mapperPlugins = Collections.singletonList(this.mapperPlugins.get(0));
                    break;
                default:
                    mapperPlugins = Collections.unmodifiableList(new ArrayList<>(this.mapperPlugins));
            }
            try {
                return new RowMapper<>(this.tClass, mapperPlugins);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        public String toString() {
            return "PrestoRowMapper.KylinRowMapperBuilder(tClass=" + this.tClass + ", mapperPlugins="
                    + this.mapperPlugins + ")";
        }
    }


}

