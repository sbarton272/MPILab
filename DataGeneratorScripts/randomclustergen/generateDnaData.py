
import argparse, random, csv, numpy

BASES = [0,1,2,3]
PROB_MUTATION = .2

# Helper functions
def generateDNA(length):
    return [random.choice(BASES) for _ in xrange(length)]

def ints2DNA(x):
	return "".join(map(int2Base,x))

def int2Base(x):
	b = x % 4
	if b == 0:
		return 'A'
	if b == 1:
		return 'C'
	if b == 2:
		return 'G'
	if b == 3:
		return 'T'

# Input args
parser = argparse.ArgumentParser(description='Generate DNA strands')
parser.add_argument('-c', metavar='--clusters', type=int,
    help='The number of clusters')
parser.add_argument('-p', metavar='--pts', type=int,
    help='The number of points per cluster')
parser.add_argument('-l', metavar='--length', type=int,
	help='The length of the DNA strands')
parser.add_argument('-o', metavar='--out', type=str,
    help='The output data file (csv)')
args = parser.parse_args()

# Generate means
means = [generateDNA(args.l) for _ in xrange(args.c)]

# Generate points that deviate from means
points = []
for m in means:
	points.extend([m + numpy.random.binomial(1,PROB_MUTATION,len(m)) for _ in xrange(args.p)])

DNA = map(ints2DNA, points)

# Open output file
writer = csv.writer(open(args.o, "w"))
for dna in DNA:
	writer.writerow([dna])
