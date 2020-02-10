package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired CategoryService categoryService;

    /**
     * 无分页方法
     * @return 返回所有数据
     * @throws Exception
     */
   /* @GetMapping("/categories")
    public List<Category> list() throws Exception {
        return categoryService.list();
    }*/

    /**
     * 实现分页方法
     * @param start 起始页
     * @param size 数量
     * @return 返回page4Navigator对象
     * @throws Exception
     */
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start",defaultValue = "0") int start , @RequestParam(value = "size",defaultValue = "5") int size) throws Exception {
        start=start<0?0:start;
        ////5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        Page4Navigator<Category> page4Navigator=categoryService.list(start,size,5);
        return page4Navigator;
    }

    /**
     * 1. 首选通过CategoryService 保存到数据库
     * 2. 然后接受上传图片，并保存到 img/category目录下
     * 3. 文件名使用新增分类的id
     * 4. 如果目录不存在，需要创建
     * 5. image.transferTo 进行文件复制
     * 6. 调用ImageUtil的change2jpg 进行文件类型强制转换为 jpg格式
     * 7. 保存图片
     * @param bean 实体类
     * @param image 上传图片
     * @param request 路径
     * @return 返回当前对象
     * @throws Exception
     */
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception{
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    private void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file=new File(imageFolder,bean.getId()+".jpg");
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage bufferedImage= ImageUtil.change2jpg(file);
        ImageIO.write(bufferedImage,"jpg",file);
    }

    /**
     * 删除方法
     * 1. 首先根据id 删除数据库里的数据
     * 2. 删除对应的文件
     * 3. 返回 null, 会被RESTController 转换为空字符串。
     * @param id 删除的id
     * @param request 获取所属图片
     * @return null
     * @throws Exception
     */
    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable(value = "id") int id,HttpServletRequest request) throws Exception{
        categoryService.delete(id);
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        File file=new File(imageFolder,id+".jpg");
        file.delete();
        return null;
    }

    /**
     * 查找方法
     * @param id 查找id
     * @return 返回实体类对象
     * @throws Exception
     */
    @GetMapping("/categories/{id}")
    public Category get(@PathVariable(value = "id") int id) throws Exception{
        Category category=categoryService.get(id);
        return category;
    }

    /**
     * 修改方法
     * 1. 获取参数名称
     这里获取参数用的是 request.getParameter("name"). 为什么不用 add里的注入一个 Category对象呢？ 因为。。。PUT 方式注入不了。。。 只能用这种方式取参数了，试了很多次才知道是这么个情况~
     2. 通过 CategoryService 的update方法更新到数据库
     3. 如果上传了图片，调用增加的时候共用的 saveOrUpdateImageFile 方法。
     4. 返回这个分类对象。
     * @param bean 实体类
     * @param image 图片方法
     * @param request 获取图片路径
     * @return 实体类
     * @throws Exception
     */
    @PutMapping("/categories/{id}")
    public Object update(MultipartFile image,Category bean,HttpServletRequest request) throws Exception {
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
        if(image!=null) {
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }
}