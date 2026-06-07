package org.example.mentalblogh.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private int code;
    private String message;
    private long total;
    private long page;
    private long pageSize;
    private long pages;
    private List<T> records;

    public static <T> PageResult<T> success(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.code = 200;
        r.message = "success";
        r.total = page.getTotal();
        r.page = page.getCurrent();
        r.pageSize = page.getSize();
        r.pages = page.getPages();
        r.records = page.getRecords();
        return r;
    }
}
