package com.xauth.core;
import com.xauth.core.types.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;


public class AuthingTest {
    static AuthenticationClient client;

    @BeforeClass
    public static void beforeAll() throws IOException {
        client = new AuthenticationClient("7f74f487bc121542ad0c7e3d");
        client.setHost("https://core.xauth.lucfish.com");
    }

    @Test
    public void loginByPhoneCode() throws IOException {
        LoginByPhoneCodeResponse response = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("15116660781", "123456"))).execute();
        System.out.println("=====loginByPhoneCode=====" + response.getResult());
        client.setAccessToken(response.getResult().getToken());
    }

    @Test
    public void userTest() throws IOException {
        LoginByPhoneCodeResponse response = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("15116660781", "123456"))).execute();
        client.setAccessToken(response.getResult().getToken());
        UserResponse userResponse = client.user(new UserParam(response.getResult().getId())).execute();
        System.out.println("=====userTest=====" + userResponse.getResult());
    }

    @Test
    public void usersTest() throws IOException {
        LoginByPhoneCodeResponse response = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("151166600000", "123456"))).execute();
        client.setAccessToken(response.getResult().getToken());
        UsersResponse usersResponse = client.users(new UsersParam(1, 2)).execute();
        System.out.println("====usersTest======" + usersResponse.getResult());
    }

    @Test
    public void checkLoginStatus() throws IOException {
        // 获取用户的 accessToken
        LoginByPhoneCodeResponse user = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("15116660781", "123456"))).execute();
        client.setAccessToken(user.getResult().getToken());
        System.out.println(client.checkLoginStatus(new CheckLoginStatusParam()).execute());
    }

    @Test
    public void updateUser() throws IOException {
        // 获取用户的 accessTokens
        LoginByPhoneCodeResponse user = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("15116660781", "123456"))).execute();
        client.setAccessToken(user.getResult().getToken());
        UpdateUserResponse response = client.updateProfile(new UpdateUserParam(user.getResult().getId(), new UpdateUserInput()
                .withName("updateName")
                .withPassword("updatePassword")
                .withProvince("广东省")
                .withCity("深圳市")
                .withRegion("南山区")
                .withStreetAddress("lucfish")
                .withFormatted("广东省深圳市南山区lucfish")
                .build())).execute();
        System.out.println("====updateUser======" + response.getResult());

    }

    @Test
    public void createUser() throws IOException {
        // 获取用户的 accessTokens
        LoginByPhoneCodeResponse user = client.loginByPhoneCode(new LoginByPhoneCodeParam(new LoginByPhoneCodeInput("15116660781", "123456"))).execute();
        client.setAccessToken(user.getResult().getToken());
        CreateUserResponse response = client.createUser(new CreateUserParam(new CreateUserInput()
                .withName("CreateName")
                .withPassword("CreatePassword")
                .build())).execute();
        System.out.println("====createUser======" + response.getResult());
    }
}
