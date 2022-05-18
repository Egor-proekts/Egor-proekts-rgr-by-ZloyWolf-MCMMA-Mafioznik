package org.prod.tgk.controllers;

import org.prod.tgk.entitys.*;
import org.prod.tgk.repositories.*;
import org.prod.tgk.services.FilesStorageService;
import org.prod.tgk.services.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ActionController {

    @Autowired
    BillRepository billRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileInfoRepository fileInfoRepository;
    @Autowired
    ToolService toolService;
    @Autowired
    FilesStorageService storageService;

    private final Path root = Paths.get("uploads");

    @GetMapping("/lk")
    public String lk(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getRole() == Role.ROLE_USER){
            model.addAttribute("u", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }else{
            model.addAttribute("u", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            model.addAttribute("orders", ordersRepository.findAll());
        }
        return "lk";
    }

    @PostMapping("/giveValue")
    public String giveValue(Model model, @RequestParam("value") Double value, RedirectAttributes redirectAttrs) {
        if (value <= 0 || value > 50) {
            redirectAttrs.addFlashAttribute("result", "Введены некорректные данные");
        } else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (user.getBills().size() == 0) {
                Bill bill = new Bill();
                bill.setValue(value);
                bill.setSupplyDate(new Date());
                billRepository.save(bill);

                user.getBills().add(bill);
                userRepository.save(user);
                redirectAttrs.addFlashAttribute("result", "Данные успешно отправлены");
            } else {
                Date nowDate = new Date();
                if (user.getBills()
                        .stream()
                        .filter(e -> e.getSupplyDate().getMonth() == nowDate.getMonth())
                        .anyMatch(e -> e.getSupplyDate().getYear() == nowDate.getYear())) {
                    redirectAttrs.addFlashAttribute("result", "Вы уже подавали данные в этом месяце");
                } else {
                    Bill bill = new Bill();
                    bill.setValue(value);
                    bill.setSupplyDate(new Date());
                    billRepository.save(bill);

                    user.getBills().add(bill);
                    userRepository.save(user);
                    redirectAttrs.addFlashAttribute("result", "Данные успешно отправлены");
                }
            }
        }
        return "redirect:/lk";
    }


    @GetMapping("/getMyValues")
    @ResponseBody
    public List<Double> getMyValues() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return toolService.getIntegeredList(user.getBills());
    }

    @PostMapping("/createOrder")
    public String createOrder(Model model,
                              @RequestParam("phone") String phone,
                              @RequestParam("descr") String descr,
                              @RequestParam("files") MultipartFile[] files,
                              RedirectAttributes redirectAttrs) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(phone + " " + descr);

        Orders orders = new Orders();
        orders.setPhone(phone);
        orders.setName(user.getName());
        orders.setSurname(user.getSurname());
        orders.setReason(descr);
        orders.setStatus(false);
        orders.setOrderDate(new Date());

        Set<FileInfo> fileInfos = new HashSet<>();

        String message = "";
        try {
            Arrays.asList(files).stream().forEach(file -> {
                try {
                    Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
                    String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf('.'));
                    String savedName = UUID.randomUUID().toString()+ext;
                    Files.move(this.root.resolve(file.getOriginalFilename()), this.root.resolve(savedName));
                    FileInfo fileInfo = new FileInfo(savedName, MvcUriComponentsBuilder.fromMethodName(ActionController.class, "getFile", savedName).build().toString(), user.getId());
                    fileInfoRepository.save(fileInfo);
                    fileInfos.add(fileInfo);
                } catch (Exception e) {
                    throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
                }
            });
            redirectAttrs.addFlashAttribute("result", "Заявка успешно отправлена");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("result", "Где-то ошибка");
        }

        orders.setFiles(fileInfos);
        ordersRepository.save(orders);
        user.getOrders().add(orders);
        userRepository.save(user);
        return "redirect:/lk";
    }

    @GetMapping("/setStatus")
    public String setStatus(@RequestParam("id") Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() == Role.ROLE_ADMIN) {
            Orders orders = ordersRepository.getById(id);
            orders.setStatus(!orders.isStatus());
            ordersRepository.save(orders);
        }
        return "redirect:/lk";
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
