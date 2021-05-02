package io.kimmking.rpcfx.demo.consumer;

import io.kimmking.rpcfx.client.ServiceRef;
import io.kimmking.rpcfx.demo.api.User;
import io.kimmking.rpcfx.demo.api.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @ServiceRef(url = "http://localhost:8080")
    private UserService userService;

    @GetMapping("/user/{id}")
    public User findById(@PathVariable("id") Integer id) {
        return userService.findById(id);
    }
}
