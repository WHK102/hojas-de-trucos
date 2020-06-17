package packagename.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Configuration
@ConfigurationProperties(prefix="app") 
public class AppSettingsBase
{
    private AppSettingsAuthentication authentication;


    public AppSettingsAuthentication getAuthentication()
    {
        return this.authentication;
    }

    public void setAuthentication(AppSettingsAuthentication authentication)
    {
        this.authentication = authentication;
    }
}