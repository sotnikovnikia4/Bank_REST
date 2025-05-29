package com.example.bankcards.service.implementation;

import com.example.bankcards.entity.Card;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyGetCard implements InvocationHandler {
    private final Card card;

    public ProxyGetCard(Card card){
        this.card = card;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return card;
    }
}
