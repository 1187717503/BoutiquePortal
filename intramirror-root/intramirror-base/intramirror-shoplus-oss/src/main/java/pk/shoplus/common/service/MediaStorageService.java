package pk.shoplus.common.service;

import pk.shoplus.common.exception.MediaStorageFailException;
import pk.shoplus.common.vo.MediaStorangeRespVo;

import java.io.InputStream;

/**
 * Created by zhuheng on 2014/12/3.
 */
public interface MediaStorageService
{

    /**
     * 存储图片,最大限制5M.可以通过URL访问到。
     * 例如:/user/20141206/uxnjaodfdnfadfaduuujpg
     *
     * @param mediaInputStream 图片流.API会对inputstream做关闭
     * @param mediaPath 文件拥有者
     * @return
     * 存储路径
     * @throws MediaStorageFailException
     */
    public MediaStorangeRespVo storageMedia(InputStream mediaInputStream, String mediaPath) throws MediaStorageFailException;



    /**
     * 存储图片,最大限制5M.可以通过URL访问到。
     * 例如:/user/20141206/uxnjaodfdnfadfaduuujpg
     *
     * @param mediaInputStream 图片流.API会对inputstream做关闭
     * @param mediaPath 文件拥有者
     * @strategy mediaPath 文件存储策略
     * 例如：
     * 1./230*240/ 将图片裁剪成230*240大小.默认为1.0
     * 2./230*240*0.9/ 将图片裁剪成230*240大小,图片质量为0.9
     * @Version 1.2 图片策略pattern为230*240
     * @Version 1.3 图片策略pattern230_240,同时生成的文件名也带上230_240
     * @return
     * 存储路径
     * @throws MediaStorageFailException
     */
    public MediaStorangeRespVo storageMedia(InputStream mediaInputStream, String mediaPath, String strategy) throws MediaStorageFailException;


    /**
     * 存储图片,最大限制5M.可以通过URL访问到。
     * 例如:/user/20141206/uxnjaodfdnfadfaduuu.jpg
     *
     * @param mediaInputStream 图片流.API会对inputstream做关闭
     * @param mediaPath 文件路径 /user/20141206/uxnjaodfdnfadfaduuu.jpg
     * @return
     * 存储路径
     * @throws MediaStorageFailException
     */
    public MediaStorangeRespVo storageMediaWithPath(InputStream mediaInputStream, String mediaPath) throws MediaStorageFailException;



}
