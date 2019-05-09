import requests
import json
import csv
import gzip

class DataExtractor:
    """
    Extracts the data for a city from OpenStreetMap, using Overpass API.
    Ref:
    OSM: https://www.openstreetmap.org
    Overpass API: https://wiki.openstreetmap.org/wiki/Overpass_API,
    http://overpass-api.de/ (server)

    The complex objects, such as ways and relations (see https://wiki.openstreetmap.org/wiki/Elements), are substituted by the geometric center.
    """

    def extract(self):
        """
        The implementation of this is inspired by:
        https://towardsdatascience.com/loading-data-from-openstreetmap-with-python-and-the-overpass-api-513882a27fd0

        Returns
        -------
        data
            The json representation of the output data
        """

        overpass_url = "http://overpass-api.de/api/interpreter"
        overpass_query = """
        [out:json];
        area["ISO3166-1"="HK"][admin_level=3];
        (node(area);
        way(area);
        rel(area);
        );
        out center;
        """
        response = requests.get(overpass_url,
                                params={'data': overpass_query})
        data = response.json()
        return data

    def toCoord(self, data):
        coords = []
        for element in data['elements']:
            if element['type'] == 'node':
                lon = element['lon']
                lat = element['lat']
                coords.append([lon, lat])
            elif 'center' in element:
                lon = element['center']['lon']
                lat = element['center']['lat']
                coords.append([lon, lat])
        return coords

def main():
    ext = DataExtractor()
    #data = ext.extract()
    with open('data.json', 'r') as infile:
        data = json.load(infile)
    coord = ext.toCoord(data)
    with gzip.open('data.csv.gz', 'wt', newline='') as outfile:
        writer = csv.writer(outfile)
        for row in coord:
            writer.writerow(row)

if __name__ == '__main__':
    main()
