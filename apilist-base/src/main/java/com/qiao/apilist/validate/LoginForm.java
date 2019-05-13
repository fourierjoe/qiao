package com.qiao.apilist.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import yhao.infra.apilist.ValidationForm;

/**
 * @Description:
 * @Created by ql on 2019/5/13/013 23:18
 * @Version: v1.0
 */
@ApiModel
public class LoginForm implements ValidationForm {
    @ApiModelProperty(
            value = "用户名",
            required = true,
            example = "user"
    )
    @NotEmpty(
            message = "用户名不得为空"
    )
    private String userName;
    @ApiModelProperty(
            value = "密码",
            example = "password"
    )
    private String password;

    public LoginForm() {
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
