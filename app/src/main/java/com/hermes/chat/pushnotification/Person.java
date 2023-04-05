package com.hermes.chat.pushnotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person {

        @SerializedName("Address")
        @Expose
        private String address;
        @SerializedName("Name")
        @Expose
        private String name;
        @SerializedName("Number")
        @Expose
        private String number;

        public Person(String address, String name, String number) {
            this.address = address;
            this.name = name;
            this.number = number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

    }