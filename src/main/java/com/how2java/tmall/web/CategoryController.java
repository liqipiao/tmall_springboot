package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}