package pk.shoplus.common.exception;

/**
 * Created by zhuheng on 2014/12/3.
 * 图片存储异常
 */
public class MediaStorageFailException extends RuntimeException
{
    public MediaStorageFailException(String message)
    {
        super(message);
    }

    public MediaStorageFailException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MediaStorageFailException(Throwable cause)
    {
        super(cause);
    }
}
