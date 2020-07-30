package model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Company {
    private List<Type> types;

    public Company() {
        types = new ArrayList<>();
    }
}
