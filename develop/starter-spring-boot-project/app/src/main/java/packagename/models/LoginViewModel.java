package packagename.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class LoginViewModel 
{
    @NotEmpty(message="{login.input.email.error.empty}")
    @NotNull(message="{login.input.email.error.empty}")
    @Size(min=3, max=256, message="{login.input.email.error.size}")
    @Email(message="{login.input.email.error.invalid}")
    //  @Pattern(regexp="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message="{backend.login.input.email.error.invalid}") 
    private String email;
    
    @NotEmpty(message="{login.input.password.error.empty}")
    @NotNull(message="{login.input.password.error.empty}")
    private String password;

    private Boolean remember;

    
    public LoginViewModel() 
    {
    }
    
    public LoginViewModel(String email, String password) 
    {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() 
    {
        return this.email;
    }
    
    public void setEmail(String email) 
    {
        this.email = email;
    }
    
    public String getPassword() 
    {
        return this.password;
    }
    
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public Boolean getRemember() 
    {
        return this.remember;
    }
    
    public void setRemember(Boolean remember) 
    {
        this.remember = remember;
    }
}
