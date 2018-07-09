package pk.shoplus.common.utils;

import pk.shoplus.common.vo.StrategyVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mingfly on 15/5/11.
 */
public class FileStrategyUtils {

    private static Logger logger= LoggerFactory.getLogger(FileStrategyUtils.class);

    public static StrategyVo parse(String strategy){

        String[] strategyArray= StringUtils.split(strategy, "*_");
        StrategyVo strategyVo=null;
        try{
            switch (strategyArray.length){
                case 2:{
                    strategyVo= new StrategyVo(Integer.parseInt(strategyArray[0]),Integer.parseInt(strategyArray[1]));
                }case 3:{
                    strategyVo= new StrategyVo(Integer.parseInt(strategyArray[0]),Integer.parseInt(strategyArray[1]),Double.parseDouble(strategyArray[2]));
                }
            }
        }catch (Exception e){
            logger.warn("图片裁剪策略格式化失败:{},error:{}",strategy,e.getMessage());
        }
        return strategyVo;
    }
}
