package org.therightstuff;

import com.google.common.collect.Sets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static Map<String, List<Image>> imagesMap = new HashMap<>();
    private static List<Image> images = new ArrayList();

    public static void main(String[] args) {
        mainInternal("/Users/ikhvostenkov/Downloads/a_example.txt");
        mainInternal("/Users/ikhvostenkov/Downloads/b_lovely_landscapes.txt");
        mainInternal("/Users/ikhvostenkov/Downloads/c_memorable_moments.txt");
        mainInternal("/Users/ikhvostenkov/Downloads/d_pet_pictures.txt");
        mainInternal("/Users/ikhvostenkov/Downloads/e_shiny_selfies.txt");
    }

    public static void mainInternal(String file) {

        try {
            Scanner s = new Scanner(new File(file));
            int n = Integer.parseInt(s.nextLine());
            for (int i = 0; i < n; i++) {
                images.add(new Image(i, s.nextLine()));
            }

            for (Image image : images) {
                for (String tag : image.tag) {
                    imagesMap.computeIfAbsent(tag, t -> new ArrayList<>()).add(image);
                }
            }

            List<Slide> slides = buildSlides(images);
            System.out.println("Total Result: " + file + " : " + calculateScore(slides));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file + "result.txt"));
            writer.append(String.valueOf(slides.size()));
            writer.newLine();
            for (Slide slide : slides) {
                for (Image image : slide.getImages()) {
                    writer.append(String.valueOf(image.id));
                    writer.append(" ");
                }
                writer.newLine();
            }

            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Slide> buildSlides(List<Image> images) {
        List<Image> horisontalImages = images.stream().filter(it -> it.orientation == Orientation.H).collect(
            Collectors.toList());
        List<Image> verticalImages = images.stream().filter(it -> it.orientation == Orientation.V).collect(
            Collectors.toList());

        List<Slide> slides = new ArrayList<>();
        slides.addAll(horisontalImages.stream()
                                      .map(Slide::new)
                                      .collect(Collectors.toList()));
        slides.addAll(combineImages(verticalImages));
        slides.sort(Comparator.comparing(slide -> slide.getTags().size()));
        List<Slide> resultSlides = new ArrayList<>(slides.size());
        for (int i = 0; i < slides.size() - 1; i++) {
            int maxScore = -1;
            int maxIndex = -1;
            if (i % 10000 == 0) {
                System.out.println(i + " / " + slides.size());
            }

            for (int j = i + 1; j < i + 100 && j < slides.size(); j++) {
                int slideScore = slides.get(i).score(slides.get(j));
                if (slideScore > maxScore) {
                    maxIndex = j;
                    maxScore = slideScore;
                }
            }
            Slide tmp = slides.get(i + 1);
            slides.set(i + 1, slides.get(maxIndex));
            slides.set(maxIndex, tmp);
            resultSlides.add(slides.get(i));
        }

//        List<Image> images = imagesMap.values().stream().max(Comparator.comparing(List::size)).orElse(
//            Collections.emptyList());
//        List<Image> imagesInSlide = new ArrayList<>();
//        for (Image image : images) {
//            if (image.orientation == Orientation.V) {
//                imagesInSlide.add(image);
//                if (imagesInSlide.size() == 2) {
//                    slides.add(new Slide(imagesInSlide.stream().map(it -> it.id).collect(Collectors.toList())));
//                    imagesInSlide = new ArrayList<>();
//                }
//            } else if (image.orientation == Orientation.H) {
//                slides.add(new Slide(Collections.singletonList(image.id)));
//            }
//        }
        resultSlides.add(slides.get(slides.size() - 1));
        return resultSlides;
    }

    private static int calculateScore(List<Slide> slides) {
        int score = 0;
        for (int i = 0; i < slides.size() - 1; i++) {
            score += slides.get(i).score(slides.get(i + 1));
        }

        return score;
    }

    private static List<Slide> combineImages(List<Image> images) {
        List<Slide> result = new ArrayList<>();
        images.sort(Comparator.comparing(image -> image.getTag().size()));
        for (int i = 0; i < images.size(); i += 2) {
            result.add(new Slide(images.get(i), images.get(i + 1)));
        }

        return result;
    }

    private static class Slide {
        public List<Image> images;
        public Set<String> tags;

        public Slide(Image image) {
            this(Collections.singletonList(image));
        }

        public Slide(Image image1, Image image2) {
            this(Arrays.asList(image1, image2));
        }

        public Slide(List<Image> images) {
            this.images = images;
            this.tags = images.stream().flatMap(it -> it.getTag().stream()).collect(Collectors.toSet());
        }

        Set<String> getTags() {
            return tags;
        }

        List<Image> getImages() {
            return images;
        }

        private int score(Slide slide) {
            int commonTags = Sets.intersection(this.getTags(), slide.getTags()).size();
            int diff1 = Sets.difference(this.getTags(), slide.getTags()).size();
            int diff2 = Sets.difference(slide.getTags(), this.getTags()).size();

            return Math.min(commonTags, Math.min(diff1, diff2));
        }

        @Override
        public String toString() {
            return "Slide{" +
                ", image=" + images +
                '}';
        }
    }

    private static class Image {
        private Integer id;
        private Set<String> tag;
        private Orientation orientation;

        public Image(Integer id, String line) {
            String[] items = line.split(" ");
            this.id = id;
            this.orientation = Orientation.valueOf(items[0]);
            int n = Integer.valueOf(items[1]);
            this.tag = new HashSet<>();
            for (int i = 2; i < items.length; i++) {
                this.tag.add(items[i]);
            }
        }

        public Integer getId() {
            return id;
        }

        public void setId(final Integer id) {
            this.id = id;
        }

        public Set<String> getTag() {
            return tag;
        }

        public void setTag(final Set<String> tag) {
            this.tag = tag;
        }

        public Orientation getOrientation() {
            return orientation;
        }

        public void setOrientation(final Orientation orientation) {
            this.orientation = orientation;
        }

        @Override
        public String toString() {
            return "Image{" +
                "id=" + id +
                ", tag=" + tag +
                ", orientation=" + orientation +
                '}';
        }
    }

    enum Orientation {
        V,
        H
    }
}
