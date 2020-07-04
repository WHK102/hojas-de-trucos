package packagename.beans;


public class AppSettingsAuthentication
{
    private Boolean multiSession = true;
    private AppSettingsAuthenticationPages pages;


    public Boolean getMultiSession()
    {
        return multiSession;
    }

    public void setMultiSession(Boolean multiSession)
    {
        this.multiSession = multiSession;
    }

    public AppSettingsAuthenticationPages getPages()
    {
        return this.pages;
    }

    public void setPages(AppSettingsAuthenticationPages pages)
    {
        this.pages = pages;
    }
}