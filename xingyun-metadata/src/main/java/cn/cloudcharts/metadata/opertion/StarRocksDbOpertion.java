package cn.cloudcharts.metadata.opertion;

import cn.cloudcharts.common.exception.ServiceException;
import cn.cloudcharts.common.support.CustomSQL;
import cn.cloudcharts.common.utils.AssertUtil;
import cn.cloudcharts.common.utils.bean.BeanUtils;
import cn.cloudcharts.metadata.model.dto.CreateTableDTO;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author wuque
 * @title: StarRocksDdlOpertion
 * @projectName xingyun
 * @description:
 * @date 2023/5/2919:57
 */
public class StarRocksDbOpertion extends AbstractDbOpertion {


    @Override
    public String buildCreateTableSql(CreateTableDTO dto) {

            String sqlId = "";

            if("1".equals(dto.getTblType())){ //外表
                sqlId = "ddl.sr.createExternalTbl";
            }

            switch (dto.getTblDataType()){ // 0-明细 1-聚合 2-更新 3-主键
                case "0":
                    sqlId = "ddl.sr.createDetailTbl";
                    break;
                case "1":
                    sqlId = "ddl.sr.createAggregateTbl";
                    break;
                case "2":
                    sqlId = "ddl.sr.createPrimaryTbl";
                    break;
                case "3":
                    sqlId = "ddl.sr.createUniqueTbl";
                    break;
                default:
                    throw new ServiceException("Please select a data model! " +
                            "StarRocks provides four table types: [0]Duplicate Key table, [1]Aggregate table, [2]Unique Key table, and [3]Primary Key table.");
            }

            Map<String, Object> map = BeanUtils.nestedObj2Map(dto);
            if(null != dto.getProperties() && dto.getProperties().size() > 0){
                map.put("properties",dto.getProperties());
            }
            return CustomSQL.getInstance().get(sqlId,map);
    }

    @Override
    public String queryAllColumns(String catalogName, String dbName, String tableName) {

        AssertUtil.checkNullString(dbName, "数据库名不可空");
        AssertUtil.checkNullString(tableName, "表名不可空");

        Map map = Maps.newHashMap();
        map.put("catalogName", StrUtil.isEmpty(catalogName)?"default_catalog":catalogName);
        map.put("dbName",dbName);
        map.put("tableName",tableName);

        return CustomSQL.getInstance().get("ops.starrocks.queryAllColumns",map);
    }

    @Override
    public String getExecQuery(String sql, Integer limit) {
        Map map = Maps.newHashMap();
        map.put("sql",sql);
        map.put("maxVal",limit);
        return CustomSQL.getInstance().get("dml.starrocks.execQuery",map);
    }

}