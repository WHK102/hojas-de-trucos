package packagename.beans;


public class AppSettingsAuthenticationPages
{
    private AppSettingsAuthenticationPagesRegistration registration;
    private AppSettingsAuthenticationPagesRecoveryPassword recoveryPassword;


    public AppSettingsAuthenticationPagesRegistration getRegistration()
    {
        return this.registration;
    }

    public void setRegistration(AppSettingsAuthenticationPagesRegistration registration)
    {
        this.registration = registration;
    }

    public AppSettingsAuthenticationPagesRecoveryPassword getRecoveryPassword()
    {
        return this.recoveryPassword;
    }

    public void setRecoveryPassword(AppSettingsAuthenticationPagesRecoveryPassword recoveryPassword)
    {
        this.recoveryPassword = recoveryPassword;
    }
}