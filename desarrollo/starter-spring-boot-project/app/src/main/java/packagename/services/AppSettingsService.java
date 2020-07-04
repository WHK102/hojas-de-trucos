package packagename.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import packagename.beans.AppSettingsBase;


@Service
public class AppSettingsService
{

    @Autowired
    private AppSettingsBase settings;


    public AppSettingsBase getSettings()
    {
        return this.settings;
    }

    public void setSettings(AppSettingsBase settings)
    {
        this.settings = settings;
    }
}