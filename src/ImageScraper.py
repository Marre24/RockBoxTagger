import os
import json
import requests
import random
import time

from bs4 import BeautifulSoup

class ImageScraper:
    user_agents = [
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edge/91.0.864.64",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0"
    ]

    headers = {
    'User-Agent': random.choice(user_agents)
    }

    @staticmethod
    def get_soup(url, headers):
        response = requests.get(url, headers=headers)
        return BeautifulSoup(response.text, 'html.parser')

    @staticmethod
    def sterilize(input):
        input = input.replace(' ', '+')
        return input
    
    @staticmethod
    def getImageFor(input):
        
        input = ImageScraper.sterilize(input)

        url = f"https://www.google.com/search?q={input}+album+cover&source=lnms&tbm=isch"
        print(f"Fetching images from: {url}")
        
        soup = ImageScraper.get_soup(url, ImageScraper.headers)

        actual_images = []
        for img_tag in soup.find_all("img"):
            if len(actual_images) >= 5:  # Stop when we have the first 5 images
                    break
            img_url = img_tag.get("src")
            if img_url and "http" in img_url:
                actual_images.append(img_url)
        
        print(f"Found {len(actual_images)} images.")

        DIR = "./CoverArt"
        if not os.path.exists(DIR):
            os.makedirs(DIR)

        query_dir = os.path.join(DIR, input.replace('+', '_'))
        if not os.path.exists(query_dir):
            os.makedirs(query_dir)

        for i, img_url in enumerate(actual_images):
            try:
                time.sleep(random.uniform(1, 3))
                print(f"Downloading {img_url}")
                img_data = requests.get(img_url).content
                
                file_path = os.path.join(query_dir, f"Cover_{i+1}.jpg")
                with open(file_path, "wb") as f:
                    f.write(img_data)
            except Exception as e:
                print(f"Could not download {img_url}: {e}")

        print(f"Downloaded {len(actual_images)} images to {query_dir}")