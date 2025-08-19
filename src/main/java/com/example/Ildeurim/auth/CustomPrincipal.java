package com.example.Ildeurim.auth;


import com.example.Ildeurim.commons.enums.UserType;

public record CustomPrincipal(Long userId, UserType userType, String phone) {}
