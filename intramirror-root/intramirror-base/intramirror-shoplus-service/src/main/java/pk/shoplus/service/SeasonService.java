package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Season;
import pk.shoplus.model.SeasonMapping;
import pk.shoplus.parameter.EnabledType;


/**
 * Created by dingyifan on 2017/7/13.
 */
public class SeasonService {
    private EntityDao<Season> seasonDao = null;

    private EntityDao<SeasonMapping> seasonMappingDao = null;

    public SeasonService(Connection conn) {
        seasonDao = new EntityDao<>(conn);
        seasonMappingDao = new EntityDao<>(conn);
    }

    public List<Season> selSeasons() throws Exception{
        try{
            Map<String,Object> map = new HashMap<>();
            map.put("enabled", EnabledType.USED);
            return seasonDao.getByCondition(Season.class,map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String,Object>> selSeasonsByCode(String season_code,int min,int max) throws  Exception{
        try {
            String sql = "";
            if(StringUtils.isNotBlank(season_code)) {
                sql = "select * from season where season_code like '%"+season_code+"%' and enabled = 1 ";
            } else {
                sql = "select * from season where 1 = 1 and enabled = 1 ";
            }
            sql += " limit "+min+"," + max;

            return seasonDao.executeBySql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<SeasonMapping> selSeasonMappings() throws Exception{
        try{
            Map<String,Object> map = new HashMap<>();
            map.put("enabled", EnabledType.USED);
            return seasonMappingDao.getByCondition(SeasonMapping.class,map);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Season createSeason(Season season) throws Exception{
        try {
            String sql = "insert into `season` ( `created_at`, `enabled`, `updated_at`, `season_code`, `chinese_desc`) " +
                    "values ( now(), '1', now(), '"+season.getSeason_code() + "', '"+season.getChinese_desc()+"')";
            seasonDao.updateBySQL(sql,null);
            return season;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public SeasonMapping createSeasonMapping(SeasonMapping seasonMapping) throws Exception{
        try {
            long id = seasonMappingDao.create(seasonMapping);
            seasonMapping.season_mapping_id = id;
            return seasonMapping;
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String,Object>> selSeasonByCode( String season_code) throws Exception{
        try {
            String sql = "select * from season where season_code =  '" + season_code + "'";
            return seasonDao.executeBySql(sql,null);
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }

    public int selSeasonSum() throws Exception{
        try {
            String sql = "select count(1) as count from season";
            List<Map<String,Object>> mapList = seasonDao.executeBySql(sql,null);
            if(mapList != null && mapList.size() > 0) {
                return Integer.parseInt(mapList.get(0).get("count").toString());
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateSeasonMapping(String season_mapping_id) throws Exception{
        try {
            String sql = "update season_mapping set enabled = 0 where season_mapping_id = " + season_mapping_id;
            seasonDao.updateBySQL(sql,null);
        } catch (Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }

    public String selSeasonCodeByBoutiqueCode(String seasonCode) throws Exception{
        try {
            if(StringUtils.isBlank(seasonCode)) {
                return "";
            }
            String sql = "select season_code from season_mapping where boutique_season_code = '" + StringUtils.trim(seasonCode) + "' and enabled = 1";
            List<Map<String,Object>> mapList  = seasonDao.executeBySql(sql,null);

            if(mapList != null && mapList.size() > 0) {
                return mapList.get(0).get("season_code").toString();
            } else {
                String selectSeasonCodeSQL = "select season_code from season_mapping where season_code = '" + StringUtils.trim(seasonCode) + "' and enabled = 1";
                mapList  = seasonDao.executeBySql(selectSeasonCodeSQL,null);
                if(mapList != null && mapList.size() >0) {
                    return mapList.get(0).get("season_code").toString();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String,Object>> selSeasonCodeByBoutiqueCode(String oldSeason,String newSeason) throws Exception{
        try {
            String sql = "select * from `season`  where `season_code`  in ('"+oldSeason+"','"+newSeason+"') order by season_sort desc";
            return seasonDao.executeBySql(sql,null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
