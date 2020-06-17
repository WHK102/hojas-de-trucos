package packagename.beans;

import java.util.Locale;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.context.i18n.LocaleContext;
import javax.servlet.http.HttpServletRequest;


public class ValidCookieLocaleResolver extends CookieLocaleResolver
{
    static String DEFAULT_LOCALE = "es";

    @Override
    public Locale resolveLocale(HttpServletRequest request)
    {
        try
        {
            return super.resolveLocale(request);
        }
        catch (Exception exception)
        {
            return Locale.forLanguageTag(DEFAULT_LOCALE);
        }
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request)
    {
        try
        {
            return super.resolveLocaleContext(request);
        }
        catch (Exception exception)
        {
            return new LocaleContext()
            {
                @Override
                public Locale getLocale()
                {
                    return Locale.forLanguageTag(DEFAULT_LOCALE);
                }
            };
        }
    }
}