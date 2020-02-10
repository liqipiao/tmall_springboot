package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    /**
     * 查询方法
     * 1. 查询地址admin_property_list 映射 AdminPageController 拿到 listProperty.html 文件
     2. listProperty.html 通过 aixos 异步访问 categories/cid/properties?start=0 拿到数据。
     这个是通过分类拿到子类数据的 RESTFULL规范 cid 是分类的id,实际运行的时候是真实的值
     3. categories/cid/properties 地址对应 PropertyController的list方法，在这个方法里通过 propertyService 获取分页数据
     * @param cid 产品id
     * @param start 起始页
     * @param size 每页显示的数据
     * @return 分页数据
     * @throws Exception
     */
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid, @RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Property> page =propertyService.list(cid, start, size,5);
        return page;
    }

    /**
     * 增加方法
     * @param bean 实体类
     * @return 实体类对象
     * @throws Exception
     */
    @PostMapping("/properties")
    public Object add(@RequestBody Property bean) throws Exception {
        propertyService.add(bean);
        return bean;
    }

    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        propertyService.delete(id);
        return null;
    }

    /**
     * 根据id查找
     * @param id 查找的id
     * @return 实体类对象
     * @throws Exception
     */
    @GetMapping("/properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        Property bean=propertyService.get(id);
        return bean;
    }

    /**
     * 修改方法
     * 1. 访问地址admin_property_edit 导致 editProperty.html 被访问
     2. editProperty.html 加载后调用 vue 的 get方法
     3. vue的get方法通过 axios 访问 properties/id 地址
     4. PropertyController 映射该地址，并通过 propertyService 获取数据
     * @param bean 实体类
     * @return 实体类对象
     * @throws Exception
     */
    @PutMapping("/properties")
    public Object update(@RequestBody Property bean) throws Exception {
        propertyService.update(bean);
        return bean;
    }
}
