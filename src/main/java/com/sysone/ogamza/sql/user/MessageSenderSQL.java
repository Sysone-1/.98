package com.sysone.ogamza.sql.user;

public class MessageSenderSQL {
    public static final String INSERT_MESSAGE = "INSERT INTO MESSAGE (id, sender_id, receiver_id, content) values (message_seq.nextval, ?,?,?)";
}
