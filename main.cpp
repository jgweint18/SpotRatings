#include <iostream>
#include <vector>
#include <map>
#include <set>
#include <fstream>
#include <sstream>
#include <string>

//rate indvidual songs


using namespace std;
vector<string> inputFile();

vector<string> inputFileLater(string);

map<string,double> rateFiles(vector<string>);

void printByRating(map<string,double>);

map<string,set<string>> songToArtist(vector<string>);

void saveToFile(map<string,double>);

void addSong(vector<string>&);

map<string,double> inputRatings(string);

int main () {
    string fileName;
    vector<string> playlistVec;
    map<string,double> songMap;
    while (true) {
        cout << "\nMenu:\n";
        cout << "A. Add Songs\n";
        cout << "1. Rate Songs\n";
        cout << "2. Show Ratings\n";
        cout << "3. Sort by Artist\n";
        cout << "4. Save Ratings To file\n";
        cout << "5. Import Ratings From File\n";
        cout << "Q. Quit\n";
        string command;
        cin >> command;
        cin.ignore();
        if (command == "1") {
            while (true) {
                cout << "\nAre you sure? This will reset all current ratings.\n";
                cout << "1. Yes\n";
                cout << "2. No\n";
                string confirmationStr;
                cin >> confirmationStr;
                cin.ignore();
                if (confirmationStr == "1") {
                    playlistVec = inputFile();
                    songMap = rateFiles(playlistVec);
                    break;
                }
                if (confirmationStr == "2") {
                    break;
                }
            }
        }
        if (command == "2") {
            while (true) {
            if (songMap.empty()==1) {
                cout << "\nYou haven't rated any songs yet!\n";
                break;
            }
                cout << "\nHow would you like to display ratings?\n";
                cout << "1. List each song\n";
                cout << "2. Sort by Rating\n";
                cout << "Q. Quit\n";
                string ratingCommand;
                cin >> ratingCommand;
                cin.ignore();
                if (ratingCommand == "1") {
                    for (auto m: songMap) {
                        cout << "Song: " << m.first << " User Rating: "<< m.second << endl;
                    }
                    break;
                }
                if (ratingCommand == "2") {
                    printByRating(songMap);
                    break;
                }
                if (ratingCommand == "Q") {
                    break;
                }
            }
        }
        if (command == "3") {
            playlistVec = inputFile();
            map <string,set<string>> artistMap = songToArtist(playlistVec);
            for (auto m : artistMap) {
                cout << m.first << ":" << endl;
                for (auto n : m.second) {
                    cout << "     " << n << endl;
                }
            }
        }

        if (command == "4") {
            saveToFile(songMap);
        }
        if (command == "5") {
            string ratingsFileName;
            cout << "Enter the name of the file that contains the ratings: ";
            getline(cin, ratingsFileName);
            songMap = inputRatings(ratingsFileName);
        }
        if (command == "A") {
            addSong(playlistVec);
        }

        if (command == "Q") {

            break;
        }
    }

    cout << "hello!";
    return 0;
}

vector<string> inputFile() {
    string fileName;
    ifstream playlist;
    while (true) {
        cout << "Enter a file name for the playlist: ";
        getline(cin,fileName);
        playlist.open(fileName);
        if (playlist.is_open()) {
            break;
        }
        else {
            cout << "Error: File not found, try another name." << endl;
        }
    }
    string word;
    vector<string> playlistVec;
    while (getline(playlist,word)) {
        playlistVec.push_back(word);
    }
    playlistVec.erase(playlistVec.begin());
    
    return playlistVec;
}

map<string,double> rateFiles(vector<string> playlistVec){
    map<string,double> songMap;
    for (auto c :playlistVec) {
        cout << endl << c << endl;
        double ratingInt;
        cout << "Enter a rating for this song (0-5): ";
        cin >> ratingInt;
        cin.ignore();
        if (ratingInt < 0) {
            break;
        }
        songMap[c]=ratingInt;
    }
    return songMap;
}

void printByRating(map<string,double> songMap) {
  map<string, double> newMap = songMap;
  map<double, set<string>> reversedMap;
  for (auto map : newMap) {
    string key = map.first;
    double value = map.second;
    reversedMap[value].insert(key);
  }
  for (auto m : reversedMap) {
    cout << m.first << ":" << endl;
    for (auto n : m.second) {
        cout << "     " << n << endl;
        }
    }
}

map<string,set<string>> songToArtist(vector<string> vec) {
    map<string,string> songArtistMap;
    map<string, set<string>> reversedMap;
    for (auto line : vec) {
        stringstream ss(line);
        string artist;
        string song;
        getline(ss,artist,',');
        getline(ss,song);
        songArtistMap[song] = artist;
    }
  for (auto map : songArtistMap) {
    string key = map.first;
    string value = map.second;
    reversedMap[value].insert(key);
  }
    return reversedMap;
}

void saveToFile(map <string,double> songMap) {
    if (songMap.empty()==1) {
        cout << "\nYou haven't rated any songs yet!\n";
        return;
    }
    string outputFile;
    cout << "Enter a name for the file (end with .csv) ";
    cin >> outputFile;
    cin.ignore();
    ofstream outstream(outputFile);
    for (auto m:songMap) {
        outstream << m.first << "|Rating: " << m.second << endl;
    }
    outstream.close();
}

void addSong (vector<string> &playlistVec) {
    string fileName;
    cout << "Enter a file name to add songs to: ";
    getline(cin,fileName);
    playlistVec = inputFileLater(fileName);
    ofstream outList(fileName, ios::app);
    if (!outList.is_open()) {
        cout << "Error: Could not open file.\n";
        return;
    }
    while (true) {
        string Name;
        string Artist;
        cout << "Enter the name of the song: ";
        getline(cin,Name);
        cout <<"Enter the artist of " << Name << ": ";
        getline(cin,Artist);
        string nextLine = "\n" + Artist + " , " + Name;
        string vecLine = Artist + " , " + Name;
        playlistVec.push_back(vecLine);
        outList << nextLine;
        string continueStr;
        cout << "Add another song?\n";
        cout << "Y. Yes\n";
        cout << "N. No\n";
        getline(cin,continueStr);
        if (continueStr == "N") {
            break;
        }
    }
    outList.close();
}

vector<string> inputFileLater(string fileName) {
    ifstream playlist;
    string word;
    playlist.open(fileName);
    vector<string> playlistVec;
    while (getline(playlist,word)) {
        playlistVec.push_back(word);
    }
    if (!playlistVec.empty()) {
        playlistVec.erase(playlistVec.begin());
    }
    return playlistVec;
}

map<string,double> inputRatings(string fileName) {
    ifstream input(fileName);
    if (!input.is_open()) {
        cout << "File does not exist.\n";
        return {};
    }
    map<string,double> songMap;
    string line;
    while (getline(input,line)) {
        stringstream ss(line);
        string Name;
        string Rating;
        getline(ss, Name, '|');
        getline(ss,Rating);
        stringstream rs(Rating);
        string temp;
        double rating;
        rs >> temp; 
        rs >> rating;
        songMap[Name] = rating;
    }
    input.close();
    return songMap;
}
