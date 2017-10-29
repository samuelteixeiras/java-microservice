package com.apssouza.controllers;

import com.apssouza.entities.Account;
import com.apssouza.exceptions.DataNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.apssouza.services.AccountService;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Account's entry points
 *
 * @author apssouza
 */
@RequestMapping("/accounts")
@RestController
public class AccountController {

    @Autowired
    AccountService userService;

    @Autowired
    public AccountController(
            AccountService userService
    ) {
        this.userService = userService;
    }

    
    @RequestMapping(value="/oauth/test", method = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String auth(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getParameterNames();
        while(headerNames.hasMoreElements()) {
            String hName = headerNames.nextElement();
            System.out.println("name=" + hName);
            System.out.println("value=" + request.getParameterValues(hName)[0]);
        }
        System.out.println("params");
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            System.out.println("name "+ entry.getKey());
            System.out.println("value "+ entry.getValue()[0]);
        }
        
//        HttpHeaders headers = request.getHeaders();
//        List<MediaType> accept = headers.getAccept();
//        String accessControlAllowOrigin = headers.getAccessControlAllowOrigin();
//        MediaType contentType = headers.getContentType();

return "test";
    }
    
    @GetMapping
    public List<Account> all() {
        return userService.all();
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid Account account) {
        Account saved = this.userService.save(account);
        Long id = saved.getId();
        if (id != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(id).toUri();
            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @PathVariable long id,
            @RequestBody @Valid Account user
    ) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> find(@PathVariable long id) {
        Optional<Account> findById = userService.findById(id);
        return findById.map(todo -> {
            return ResponseEntity.ok(todo);
        }).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
    }

    @GetMapping("search")
    public ResponseEntity<?> find(@RequestParam("email") String email) {
        Optional<Account> account = userService.findByEmail(email);
        return account.map(a -> {
            return ResponseEntity.ok(a);
        }).orElseThrow(
                () -> new DataNotFoundException("user not found")
        );
    }

}
