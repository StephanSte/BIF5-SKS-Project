package com.example.begzug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping(path="/api") // Maps all other REST calls to /api/*
public class MainController {
    @Autowired // Spring (Boot) automtaically takes care of adding this to the class
    private AuthorRepository authorRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SightRepository sightRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Adds an author to the database.
     * Mainly just used for testing convenience, as adding authors is technically not
     * in the scope of the project.
     */
    @PostMapping(path="/add") // Map ONLY POST Requests
    public String addNewUser (@RequestParam String name, @RequestParam String surname
            , @RequestParam String email, Model model) {
        Author n = new Author();
        n.setName(name);
        n.setSurname(surname);
        n.setEmail(email);

        authorRepository.save(n);
        return getAllAuthors(model);
    }
    /**
     * Produces a page that allows the user to input an article.
     * Mainly just this dynamic because of the varying list of sights.
     */
    @GetMapping("/writearticle")
    public String writeArticle(Model model){
        List<Sight> searchResults = sightRepository.findAll();
        model.addAttribute("sights", searchResults);
        return "Article";
    }
    /**
     * Adds an article specified by parameters of the request.
     * In a production environment, one would have to somehow authenticate the author,
     * but this is not in the scope of this project.
     */
    @PostMapping("/addA")
    public String addNewArticle (@RequestParam String title, @RequestParam String author, @RequestParam String text, @RequestParam String sight, Model model) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        List<Author> searchResults = authorRepository.findByName(author);
        Article a = new Article();
        System.out.println(text);
        a.setTitle(title);
        a.setText(text);
        // clicks are automatically set to 0
        if (searchResults.size() < 1) {
            return "failure.html";
        }
        a.setAuthor(authorRepository.findByName(author).get(0));
        a.setSight(sightRepository.findByName(sight).get(0));
        System.out.println(a.getText());
        articleRepository.save(a);

        return getSingleArticle(model, a.getId());
    }

    /**
     * Produces a page showing all articles.
     */
    @GetMapping("/articles")
    public String showAllArticles(Model model)
    {
        model.addAttribute("articles", articleRepository.findAll());
        return "ArticleOverview";
    }

    /**
     * Produces a page showing all registered authors.
     */
    @GetMapping(path="/allAuthors")
    public String getAllAuthors(Model model) {
        model.addAttribute("authors", authorRepository.findAll());
        return "AllAuthors";
    }

    /**
     * Pays a single author. Likely to be deprecated soon, as all authors should be paid simultaneously.
     */
    @GetMapping(path="/payment")
    public String getAllClicks(@RequestParam("authorname") String authorName, Model model) {
        System.out.println("Looking for " + authorName);
        List<Author> searchResults = authorRepository.findByName(authorName);
        if (searchResults.size() < 1) {
            return "failure.html";
        }
        Author author = authorRepository.findByName(authorName).get(0);
        List<Article> allArticles = articleRepository.findArticlesByAuthor(author);
        System.out.println("List Count: " + allArticles.size());
        model.addAttribute("author", author);
        int sumOfClicks = 0;
        for (Article art : allArticles) {
            sumOfClicks += art.getClicks();
        }
        int eurosPaid = sumOfClicks / 100;
        int centsPaid = sumOfClicks % 100;
        model.addAttribute("euros", eurosPaid);
        model.addAttribute("cents", centsPaid);
        model.addAttribute("totalclicks", sumOfClicks);

        return "Payment";
    }

    /**
     * Shows a single article. Also increments both the total and the monthly click counter.
     */
    @GetMapping(path="/articleid")
    public String getSingleArticle(Model model, @RequestParam("id") int id) {
        model.addAttribute("article", articleRepository.findById(id));
        // Life is beautiful.
        Article ViewedArticle = articleRepository.findById(id);
        ViewedArticle.setClicks(ViewedArticle.getClicks() + 1);
        ViewedArticle.setPendingClicks(ViewedArticle.getPendingClicks()+ 1);
        articleRepository.save(ViewedArticle);

        return "SingleArticle";
    }

    /**
     *  Pays the authors. In a production environment, this would automatically be called monthly,
     *  but for demonstration purposes, this version includes a simple button that handles the payment.
     */
    @GetMapping(path="/payauthors")
    public String payAuthors(Model model) {
        List<Author> allAuthors = authorRepository.findAll();
        Map<Author,Integer> amountsToPay = new HashMap<>();
        for (Author author : allAuthors) {
            List<Article> myArticles = articleRepository.findArticlesByAuthor(author);
            int amountToPay = 0;
            for (Article article : myArticles) {
                amountToPay += article.getPendingClicks();
                article.setPendingClicks(0);
                articleRepository.save(article);
            }
            if (amountToPay == 0)
                continue;
            amountsToPay.put(author, amountToPay);
            String message = author.getName() + " " + amountToPay;
            kafkaTemplate.send("payment", message);
        }


        model.addAttribute("authors", amountsToPay);

        return "PaymentResult";
    }
    /**
     * Produces a page showing all sights in the database.
     * Also shows statistic about all articles about each sight, and their respective clicks / monthly clicks.
     */
    @GetMapping("/sights")
    public String getSightStatistics(Model model) {
        List<Sight> allSights = sightRepository.findAll();
        Map<Sight, Integer> sightClicks = new HashMap<>();
        Map<Sight, Integer> unpaidSightClicks = new HashMap<>();
        for (Sight sight : allSights)
        {
            List<Article> sightArticles = articleRepository.findArticlesBySight(sight);
            int totalClicks = 0;
            int totalUnpaidClicks = 0;
            for (Article article : sightArticles) {
                totalClicks += article.getClicks();
                totalUnpaidClicks += article.getPendingClicks();
            }

            sightClicks.put(sight, totalClicks);
            unpaidSightClicks.put(sight, totalUnpaidClicks);
        }

        model.addAttribute("sightclicks", sightClicks);
        model.addAttribute("unpaidsightclicks", unpaidSightClicks);

        return "SightStatistic";
    }

}