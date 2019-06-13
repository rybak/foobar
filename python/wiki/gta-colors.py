import re


#
# Helper Python script to generate contrast colors for templates
# used on the GTA wiki
#
# Formulas for contrast and luminance where taken from 
# 
# - https://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef
# - https://www.w3.org/TR/2008/REC-WCAG20-20081211/#relativeluminancedef
#
# See also https://stackoverflow.com/questions/7260989/how-to-pick-good-contrast-rgb-colors-programmatically
#


def lum(r,g,b):
    RsRGB = r/255
    GsRGB = g/255
    BsRGB = b/255

    if RsRGB <= 0.03928:
        R = RsRGB/12.92
    else:
        R = ((RsRGB+0.055)/1.055) ** 2.4
    if GsRGB <= 0.03928:
        G = GsRGB/12.92
    else:
        G = ((GsRGB+0.055)/1.055) ** 2.4
    if BsRGB <= 0.03928:
        B = BsRGB/12.92 
    else:
        B = ((BsRGB+0.055)/1.055) ** 2.4

    return 0.2126 * R + 0.7152 * G + 0.0722 * B


def parse_hex(h):
    return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))


def to_hex(c):
    return "{0:02X}{1:02X}{2:02X}".format(c[0], c[1], c[2])


def contrast(light, dark):
    L1 = lum(*light)
    L2 = lum(*dark)
    return (L1 + 0.05) / (L2 + 0.05)


white=parse_hex('FFFFFF')
black=parse_hex('000000')


def choose_contrast(c):
    white_contrast = contrast(white, c)
    black_contrast = contrast(c, black)
    if black_contrast > white_contrast:
        return black
    else:
        return white


DEBUG = False
if DEBUG:
    grey=parse_hex('808080')
    red=parse_hex('FF0000')
    print(lum(*white))
    print(lum(*black))
    print(lum(*grey))
    print(lum(*red))
    
    print()
    print(choose_contrast(white))
    print(choose_contrast(black))
    print(choose_contrast(grey))
    print(choose_contrast(red))
    
    print()
    print(to_hex(choose_contrast(white)))
    print(to_hex(choose_contrast(black)))
    print(to_hex(choose_contrast(grey)))
    print(to_hex(choose_contrast(red)))


# input file: code of SwitchColor template, for example
# https://gta.fandom.com/wiki/Template:CarColIV/SwitchColor
lines = open('gta-iii-colors.txt', 'r').readlines()
res = {}
res[white]=[]
res[black]=[]
for ln in lines:
    if '=' in ln:
        # |0=000000<!--Black (0)-->
        m = re.search('^.([0-9]*)=([0-9A-Fa-f]*)', ln)
        if not m: continue
        if DEBUG:
            print(m.group(1), m.group(2))
        num = int(m.group(1))
        c = parse_hex(m.group(2))
        res[choose_contrast(c)].append(num)
    else:
        if DEBUG:
            print(ln)


# output file: main part of the SwitchContrastColor template, for example
# https://gta.fandom.com/wiki/Template:CarColIV/SwitchContrastColor
with open('res-iii.txt', 'w') as f:
    # boiler plate at the top
    f.write('{{#switch: {{{1}}}\n')
    # main code
    for contrast_color, nums in res.items():
        for n in nums:
            f.write('\n|{}'.format(n))
        f.write(' = {}\n'.format(to_hex(contrast_color)))
    # boiler plate at the bottom
    f.write('\n|#default = FFFFFF\n}}<noinclude>\n{{Documentation}}\n</noinclude>')



