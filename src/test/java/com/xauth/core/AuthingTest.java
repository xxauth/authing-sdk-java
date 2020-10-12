package com.xauth.core;

import com.xauth.core.types.CheckLoginStatusParam;
import com.xauth.core.types.ExtendUser;
import com.xauth.core.types.LoginByEmailParam;
import com.xauth.core.types.LoginByOidcParam;
import com.xauth.core.types.LoginByPhonePasswordParam;
import com.xauth.core.types.LoginByUsernameParam;
import com.xauth.core.types.OidcUserParam;
import com.xauth.core.types.ReadOauthListParam;
import com.xauth.core.types.RefreshOidcTokenParam;
import com.xauth.core.types.RefreshTokenParam;
import com.xauth.core.types.RegisterParam;
import com.xauth.core.types.RemoveUsersParam;
import com.xauth.core.types.SendResetPasswordEmailParam;
import com.xauth.core.types.SendVerifyEmailParam;
import com.xauth.core.types.UpdateUserParam;
import com.xauth.core.types.UserParam;
import com.xauth.core.types.UserPatchParam;
import com.xauth.core.types.UserRegisterInput;
import com.xauth.core.types.UserUpdateInput;
import com.xauth.core.types.UsersParam;

import com.xauth.core.types.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthingTest {
    static Authing client;

    @BeforeClass
    public static void beforeAll() {
        client = new Authing("5e72d65c77932a59a266a5ca", "699b99005bdf51d5f7ca97014ed9fdea");
    }

    @Test
    public void loginBySecret() throws IOException {
        System.out.println(client.loginBySecret().execute());
    }

    @Test
    public void loginByEmail() throws IOException {
        System.out.println(client.loginByEmail(new LoginByEmailParam().email("test@test.com").password("123456").build()).execute());
    }

    @Test
    public void loginByUsername() throws IOException {
        System.out.println(client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute());
    }

    @Test
    public void loginByPhonePassword() throws IOException {
        System.out.println(client.loginByPhonePassword(new LoginByPhonePasswordParam().phone("17611161550").password("123456").build()).execute());
    }

    @Test
    public void loginByOidc() throws IOException {
        System.out.println(client.loginByOidc(new LoginByOidcParam("5e72d72e3798fb03e1d57b13", "931f19ce2161e5560c072f586c706ee6").initWithEmail("test@test.com", "123456").build()).execute());
    }

    @Test
    public void oidcUser() throws IOException {
        System.out.println(client.oidcUser(new OidcUserParam("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im51U1A3aHdVdGIwZUszaUhsNTY2TUJvakUyY282VldzRU1mZUFvMHZrNWsifQ.eyJqdGkiOiJqc2ZMMlIxTzFGSDRqTXFzZX5MQTUiLCJzdWIiOiI1ZjBlNmYzZjA2YzViNzZiN2I4YTg2NWQiLCJpc3MiOiJodHRwczovL2F1dGhpbmctbmV0LXNkay1kZW1vLmF1dGhpbmcuY24vb2F1dGgvb2lkYyIsImlhdCI6MTU5ODUwNjg2NCwiZXhwIjoxNTk4NTEwNDY0LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIHBob25lIG9mZmxpbmVfYWNjZXNzIiwiYXVkIjoiNWU3MmQ3MmUzNzk4ZmIwM2UxZDU3YjEzIn0.TTaFFHtAPv11z0_ghr_cquNFXplImFurPdCg683eDtZSm3IWdg6Nd9RjA5Y96HElxbgRSkH05aXlO7kr-NucgGL_q082L-1h99ju_2xN8lQ_vcIeBFp2qDTwydp9icHvOCz0Etg1rHRPm625sfu3Cza8ptpM4LDHua6xyi7OVcLF92oNjXErqopwRoG-BpdiPWycMi0SQqNBZOOhUnsUfG1ddP95STodXxskRZvjxkaLteyC3lTClnzq39lkYTMZ_oEosl7mzu7JarFMwiXVkpyvFkOpxdww7zs5MAZLKsKXzQ57pVmEiaRSaPw9qVd2f_OCZJ6dZMxgOQBWn8iPUQ").build()).execute());
    }

    @Test
    public void refreshOidcToken() throws IOException {
        System.out.println(client.refreshOidcToken(new RefreshOidcTokenParam("5e72d72e3798fb03e1d57b13", "931f19ce2161e5560c072f586c706ee6","40C1CpPQvU2RjSPY1c8nWjmblwa").build()).execute());
    }

    @Test
    public void register() throws IOException {
        String username = System.currentTimeMillis() + "";
        UserRegisterInput input = new UserRegisterInput().username(username).password("123456").build();
        System.out.println(client.register(new RegisterParam().userInfo(input).build()).execute());
    }

    @Test
    public void refreshToken() throws IOException {
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);
        ExtendUser user = client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute().getResult();
        System.out.println(client.refreshToken(new RefreshTokenParam().user(user.get_id()).build()).execute());
    }

    @Test
    public void user() throws IOException {
        ExtendUser user = client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute().getResult();
        client.setAccessToken(user.getToken());
        System.out.println(client.user(new UserParam().token(user.getToken()).build()).execute());
    }

    @Test
    public void updateUser() throws IOException {
        ExtendUser user = client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute().getResult();
        client.setAccessToken(user.getToken());
        UserUpdateInput options = new UserUpdateInput()._id(user.get_id()).nickname("nickname").build();
        System.out.println(client.updateUser(new UpdateUserParam().options(options).build()).execute());
    }

    @Test
    public void checkLoginStatus() throws IOException {
        ExtendUser user = client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute().getResult();
        client.setAccessToken(user.getToken());
        System.out.println(client.checkLoginStatus(new CheckLoginStatusParam().token(user.getToken()).build()).execute());
    }

    @Test
    public void readOauthList() throws IOException {
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);
        System.out.println(client.readOauthList(new ReadOauthListParam().build()).execute());
    }

    @Test
    public void sendVerifyEmail() throws IOException {
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);
        System.out.println(client.sendVerifyEmail(new SendVerifyEmailParam().email("1498881550@qq.com").build()).execute());
    }

    @Test
    public void sendPhoneVerifyCode() throws IOException {
        System.out.println(client.sendPhoneVerifyCode("17611161550").execute());
    }

    @Test
    public void sendResetPasswordEmail() throws IOException {
        client.sendResetPasswordEmail(new SendResetPasswordEmailParam().email("1498881550@qq.com").build()).execute();
    }

    @Test
    public void userPatch() throws IOException {
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);
        ExtendUser user = client.loginByUsername(new LoginByUsernameParam().username("test").password("123456").build()).execute().getResult();
        System.out.println(client.userPatch(new UserPatchParam().ids(user.get_id()).build()).execute());
    }

    @Test
    public void users() throws IOException {
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);
        System.out.println(client.users(new UsersParam().page(1).count(2).build()).execute());
    }

    @Test
    public void removeUsers() throws IOException {
        // 获得管理员权限
        String token = client.loginBySecret().execute().getResult();
        client.setAccessToken(token);

        // 注册用户
        String username = System.currentTimeMillis() + "";
        UserRegisterInput input = new UserRegisterInput().username(username).password("123456").build();
        ExtendUser user = client.register(new RegisterParam().userInfo(input).build()).execute().getResult();

        // 删除用户
        List<String> ids = new ArrayList<String>();
        ids.add(user.get_id());
        System.out.println(client.removeUsers(new RemoveUsersParam().ids(ids).build()).execute());
    }
}
