package com.planningpoker.controller;

import com.planningpoker.controller.DTO.CreateUserBody;
import com.planningpoker.exceptions.NotFoundException;
import com.planningpoker.model.UserModel;
import com.planningpoker.service.interfaces.RoomService;
import com.planningpoker.service.interfaces.UserService;
import com.planningpoker.utilities.ErrorObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserModel>> getUserById(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<Optional<UserModel>>(userService.getUserById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity getUsersFromRoom(@RequestParam String roomCode) {
        try {
            List<UserModel> users = roomService.getUsersFromRoom(roomCode);
            return new ResponseEntity(users, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<UserModel> postUser(@RequestBody CreateUserBody user) {
        try {
            UserModel newUser = userService.createUser(user.getUsername(), user.getRoomCode());
            roomService.addUserToRoom(user.getRoomCode(), newUser);
            return new ResponseEntity<UserModel>(newUser, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@RequestParam ObjectId userId, @RequestParam String roomCode) {
        try {
            userService.deleteUser(userId);
            roomService.deleteUserFromRoom(roomCode, userId);
            return new ResponseEntity(HttpStatus.OK);
        }  catch (NotFoundException e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable ObjectId id, @RequestParam String username) {
        try {
            userService.updateUsername(username, id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NotFoundException error) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
