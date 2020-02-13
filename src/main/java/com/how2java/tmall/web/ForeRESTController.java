package com.how2java.tmall.web;

import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang.math.RandomUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    /**
     * 查询分类方法
     * 1. 查询所有分类
     2. 为这些分类填充产品集合
     3. 为这些分类填充推荐产品集合
     4. 移除产品里的分类信息，以免出现重复递归
     * @return
     */
    @GetMapping("/forehome")
    public Object home(){
        List<Category> cs=categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    /**
     * 注册方法
     * @param user
     * @return
     */
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name=user.getName();
        String password=user.getPassword();
        name= HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist=userService.isExist(name);
        if (exist){
            String message="用户名已经被使用，请重新输入";
            return Result.fail(message);
        }
        user.setPassword(password);
        userService.add(user);
        return Result.success();
    }

    /**
     * 登录方法
     * 1. 账号密码注入到 userParam 对象上
     2. 把账号通过HtmlUtils.htmlEscape进行转义
     3. 根据账号和密码获取User对象
     3.1 如果对象为空，则返回错误信息
     3.2 如果对象存在，则把用户对象放在 session里，并且返回成功信息
     * @param userParam 用户信息
     * @param session 保存用户信息session
     * @return
     */
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        String name=userParam.getName();
        name=HtmlUtils.htmlEscape(name);
        User user=userService.get(name,userParam.getPassword());
        if (null==user){
            String message="账号或密码错误";
            return Result.fail(message);
        }else {
            session.setAttribute("user",user);
            return Result.success();
        }
    }

    /**
     * 产品详情方法
     * 1. 获取参数pid
     2. 根据pid获取Product 对象product
     3. 根据对象product，获取这个产品对应的单个图片集合
     4. 根据对象product，获取这个产品对应的详情图片集合
     5. 获取产品的所有属性值
     6. 获取产品对应的所有的评价
     7. 设置产品的销量和评价数量
     8. 把上述取值放在 map 中
     9. 通过 Result 把这个 map 返回到浏览器去
     * @param pid 产品id
     * @return
     */
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid){
        Product product=productService.get(pid);
        List<ProductImage> productSingleImages=productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages=productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        List<PropertyValue> propertyValues=propertyValueService.list(product);
        List<Review> reviews=reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProdutImage(product);
        Map<String,Object> map=new HashMap<>();
        map.put("product",product);
        map.put("pvs",propertyValues);
        map.put("reviews",reviews);
        return Result.success(map);
    }

    /**
     * 模态框登录
     * 获取session中的"user"对象
     如果不为空，即表示已经登录，返回 Result.success()
     如果为空，即表示未登录，返回 Result.fail("未登录");
     * @param session 保存用户信息的session
     * @return
     */
    @GetMapping("/forecheckLogin")
    public Object chechLogin(HttpSession session){
        User user= (User) session.getAttribute("user");
        if (null!=user){
            return Result.success();
        }
        return Result.fail("请先登录");
    }

    /**
     * 排序方法
     * @param cid 产品id
     * @param sort 排序名称
     * @return
     */
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return c;
    }

    /**
     * 模糊查询方法
     * 1. 获取参数keyword
     2. 根据keyword进行模糊查询，获取满足条件的前20个产品
     3. 为这些产品设置销量和评价数量
     4. 返回这个产品集合
     * @param keyword 查询字符串
     * @return
     */
    @PostMapping("/foresearch")
    public Object search(String keyword){
        if (null==keyword){
            keyword="";
        }
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    /**
     * 立即购买方法
     * 1. 获取参数pid
     2. 获取参数num
     3. 根据pid获取产品对象p
     4. 从session中获取用户对象user
     接下来就是新增订单项OrderItem， 新增订单项要考虑两个情况
     a. 如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
     a.1 基于用户对象user，查询没有生成订单的订单项集合
     a.2 遍历这个集合
     a.3 如果产品是一样的话，就进行数量追加
     a.4 获取这个订单项的 id
     b. 如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
     b.1 生成新的订单项
     b.2 设置数量，用户和产品
     b.3 插入到数据库
     b.4 获取这个订单项的 id
     5.返回当前订单项id
     6. 在页面上，拿到这个订单项id，就跳转到 location.href="buy?oiid="+oiid;
     * @param pid 产品id
     * @param num 数量
     * @param session 保存用户信息的session
     * @return
     */
    @GetMapping("forebuyone")
    public Object buyone(int pid,int num,HttpSession session){
        return buyoneAndAddCart(pid,num,session);
    }

    private Object buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product=productService.get(pid);
        int oiid=0;
        User user= (User) session.getAttribute("user");
        boolean found=false;
        List<OrderItem> ois=orderItemService.listByUser(user);
        for (OrderItem oi : ois){
            if (oi.getProduct().getId()==product.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found=true;
                oiid=oi.getId();
                break;
            }
        }
        if (!found){
            OrderItem orderItem=new OrderItem();
            orderItem.setUser(user);
            orderItem.setProduct(product);
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid=orderItem.getId();
        }
        return oiid;
    }

    /**
     * 结算信息
     * 1. 通过字符串数组获取参数oiid
     2. 准备一个泛型是OrderItem的集合ois
     3. 根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入ois集合中
     4. 累计这些ois的价格总数，赋值在total上
     5. 把订单项集合放在session的属性 "ois" 上
     6. 把订单集合和total 放在map里
     7. 通过 Result.success 返回
     * @param oiid 订单id
     * @param session
     * @return
     */
    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        session.setAttribute("ois", orderItems);
        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 加入购物车方法
     * @param pid 产品id
     * @param num 数量
     * @param session
     * @return
     */
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }

    /**
     * 查看购物车方法
     * 1. 通过session获取当前用户
     2. 获取为这个用户关联的订单项集合 ois
     3. 设置图片
     4. 返回这个订单项集合
     * @param session
     * @return
     */
    @GetMapping("forecart")
    public Object cart(HttpSession session){
        User user= (User) session.getAttribute("user");
        List<OrderItem> ois=orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    /**
     * 调整订单数量
     * 1. 判断用户是否登录
     2. 获取pid和number
     3. 遍历出用户当前所有的未生成订单的OrderItem
     4. 根据pid找到匹配的OrderItem，并修改数量后更新到数据库
     5. 返回 Result.success()
     * @param session 保存用户信息的session
     * @param pid 产品id
     * @param num 数量
     * @return
     */
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session,int pid,int num){
        User user= (User) session.getAttribute("user");
        if (null==user){
            return Result.fail("请您先登录");
        }
        List<OrderItem> ois=orderItemService.listByUser(user);
        for (OrderItem oi : ois){
            if (oi.getProduct().getId()==pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }

    /**
     * 删除订单方法
     * @param session 保存用户信息的session
     * @param oiid 订单id
     * @return
     */
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user) {
            return Result.fail("未登录");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    /**
     * 生成订单方法
     * 1. 从session中获取user对象
     2. 根据当前时间加上一个4位随机数生成订单号
     3. 根据上述参数，创建订单对象
     4. 把订单状态设置为等待支付
     5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中 )
     7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
     8. 统计本次订单的总金额
     9. 返回总金额
     * @param order
     * @param session
     * @return
     */
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody  Order order,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("请您先登录");
        }
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(order, ois);
        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 支付成功方法
     * 1.1 获取参数oid
     1.2 根据oid获取到订单对象order
     1.3 修改订单对象的状态和支付时间
     1.4 更新这个订单对象到数据库
     1.5 返回订单
     * @param oid 订单id
     * @return
     */
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    /**
     * 查看订单方法
     * 1. 通过session获取用户user
     2. 查询user所有的状态不是"delete" 的订单集合os
     3. 为这些订单填充订单项
     4. 返回 订单集合
     * @param session 保存用户信息的session
     * @return
     */
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user) {
            return Result.fail("未登录");
        }
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    /**
     * 确认收货方法
     * 2.1 获取参数oid
     2.2 通过oid获取订单对象o
     2.3 为订单对象填充订单项
     2.4 把订单项上的订单对象移除，否则会导致重复递归
     2.5 返回订单对象
     * @param oid
     * @return
     */
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid){
        Order order=orderService.get(oid);
        orderItemService.fill(order);
        orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    /**
     * 确认收货成功方法
     * 3.1 获取参数oid
     3.2 根据参数oid获取Order对象o
     3.3 修改对象o的状态为等待评价，修改其确认支付时间
     3.4 更新到数据库
     3.5 返回成功
     * @param oid 订单id
     * @return
     */
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order order=orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return Result.success();
    }

    /**
     * 删除已购买订单方法
     * .1 获取参数oid
     1.2 根据oid获取订单对象o
     1.3 修改状态
     1.4 更新到数据库
     1.5 返回成功
     * @param oid
     * @return
     */
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order order=orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return Result.success();
    }

    /**
     * 评价方法
     * 3.1 获取参数oid
     3.2 根据oid获取订单对象o
     3.3 为订单对象填充订单项
     3.4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片
     3.5 获取这个产品的评价集合
     3.6 为产品设置评价数量和销量
     3.7 把产品，订单和评价集合放在map上
     3.8 通过 Result 返回这个map
     * @param oid 订单id
     * @return
     */
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);
        return Result.success(map);
    }

    /**
     * 提交评价方法
     * 1.1 获取参数oid
     1.2 根据oid获取订单对象o
     1.3 修改订单对象状态
     1.4 更新订单对象到数据库
     1.5 获取参数pid
     1.6 根据pid获取产品对象
     1.7 获取参数content (评价信息)
     1.8 对评价信息进行转义，道理同注册ForeRESTController.register()
     1.9 从session中获取当前用户
     1.10 创建评价对象review
     1.11 为评价对象review设置 评价信息，产品，时间，用户
     1.12 增加到数据库
     1.13.返回成功
     * @param session 保存用户信息的session
     * @param oid 订单id
     * @param pid 产品id
     * @param content 评价信息
     * @return
     */
    @PostMapping("foredoreview")
    public Object doreview(HttpSession session,int oid,int pid,String content){
        Order order=orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);
        Product product=productService.get(pid);
        content=HtmlUtils.htmlEscape(content);
        User user= (User) session.getAttribute("user");
        Review review=new Review();
        review.setContent(content);
        review.setProduct(product);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}
