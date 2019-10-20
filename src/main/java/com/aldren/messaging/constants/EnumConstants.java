package com.aldren.messaging.constants;

public class EnumConstants {

    public enum UserRole {
        ADMIN("Admin"),
        USER("User");

        private final String text;

        UserRole(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum UserStatus {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE");

        private final String text;

        UserStatus(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum MessageStatus {
        READ("READ"),
        UNREAD("UNREAD");

        private final String text;

        MessageStatus(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
