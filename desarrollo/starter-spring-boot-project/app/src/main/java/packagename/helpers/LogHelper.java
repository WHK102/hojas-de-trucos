package packagename.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogHelper
{
    Logger logger = LoggerFactory.getLogger(LogHelper.class);

    public void t(String message)
    {
        logger.trace(message);
    }

    public void d(String message)
    {
        logger.debug(message);
    }

    public void i(String message)
    {
        logger.info(message);
    }

    public void w(String message)
    {
        logger.warn(message);
    }

    public void e(String message)
    {
        logger.error(message);
    }

    public void raw(String message)
    {
        System.out.println(message);
    }
}
