package org.prod.tgk.services;

import org.prod.tgk.entitys.Bill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolService {
    public List<Double> getIntegeredList(List<Bill> fromStorage) {
        List<Double> result = new ArrayList<Double>(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0));

        if (fromStorage.size() == 0) {
            return result;
        }

        int size = fromStorage.size()-1;
        for (int i = 11; i >= 0; i--) {
            Bill check = fromStorage.stream()
                    .sorted(Comparator.comparing(Bill::getSupplyDate))
                    .collect(Collectors.toList()).get(size);
            System.out.println(check);
            if (i == check.getSupplyDate().getMonth()) {
                result.set(i, check.getValue());
                if (size != 0) {
                    size--;
                }
            } else {
                result.set(i, 0.0);
            }
        }

        System.out.println(result);
        return result;
    }
}
