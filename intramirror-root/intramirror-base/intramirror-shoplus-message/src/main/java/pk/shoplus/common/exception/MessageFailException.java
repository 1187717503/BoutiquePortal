package pk.shoplus.common.exception;

/**
 * Created by zhuheng on 2014/12/3.
 * 图片存储异常
 */
public class MessageFailException extends RuntimeException
{
    public MessageFailException(String message)
    {
        super(message);
    }

    public MessageFailException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MessageFailException(Throwable cause)
    {
        super(cause);
    }
}
