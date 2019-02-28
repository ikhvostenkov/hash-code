package org.therightstuff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        if (args.length < 1) {
            throw new IllegalArgumentException("No file is provided!");
        }

        try {
            Scanner s = new Scanner(new File(args[0]));
            int n = Integer.parseInt(s.nextLine());
            for (int i = 0; i < n; i++) {
                images.add(new Image(i, s.nextLine()));
            }

            for (Image image : images) {
                for (String tag : image.tag) {
                    imagesMap.computeIfAbsent(tag, t -> new ArrayList<>()).add(image);
                }
            }

            List<Slide> slides = buildSlides();
            BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
            writer.append(String.valueOf(slides.size()));
            writer.newLine();
            for (Slide slide : slides) {
                for (Integer imageId : slide.images) {
                    writer.append(String.valueOf(imageId));
                    writer.append(" ");
                }
                writer.newLine();
            }

            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Slide> buildSlides() {
        List<Slide> slides = new ArrayList<>();
        Set<Integer> usedImages = new HashSet<>();
        List<Image> images = imagesMap.values().stream().max(Comparator.comparing(List::size)).orElse(
            Collections.emptyList());
        List<Image> imagesInSlide = new ArrayList<>();
        for (Image image : images) {
            if (image.orientation == Orientation.V ) {
                imagesInSlide.add(image);
                if (imagesInSlide.size() == 2) {
                    slides.add(new Slide(imagesInSlide.stream().map(it -> it.id).collect(Collectors.toList())));
                    imagesInSlide = new ArrayList<>();
                }
            } else if (image.orientation == Orientation.H) {
                slides.add(new Slide(Collections.singletonList(image.id)));
            }
        }

        return slides;
    }

    private static class Slide {
        public List<Integer> images;

        public Slide(List<Integer> imageId) {
            images = imageId;
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
    }

    enum Orientation {
        V,
        H
    }
}
