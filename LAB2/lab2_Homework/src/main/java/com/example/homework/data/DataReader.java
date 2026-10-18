package com.example.homework.data;

import java.io.IOException;
import java.util.List;
import com.example.homework.model.Product;

public interface DataReader {

    List<Product> read() throws IOException;
}

