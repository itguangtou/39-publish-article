package com.itguangtou.handler;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private int code;
    private String message;
    private PageData<T> data;

    @Data
    public static class PageData<T> {
        private List<T> records;
        private long total;
        private long current;
        private long size;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setCode(200);
        result.setMessage("成功");

        PageData<T> data = new PageData<>();
        data.setRecords(page.getRecords());
        data.setTotal(page.getTotal());
        data.setCurrent(page.getCurrent());
        data.setSize(page.getSize());
        result.setData(data);
        return result;
    }
}
