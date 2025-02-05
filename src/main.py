from ImageScraper import ImageScraper

class Main:
    @staticmethod
    def main():
        while True:
            i = input("Write an album cover to download and exit to leave: ")
            if i.lower() == "exit":
                break
            ImageScraper.getImageFor(i)

if __name__ == "__main__":
    Main.main()