$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$brands = @(
	@{ name = "apexlyn"; body = "#C53A36"; light = "#F27866"; dark = "#571A20"; stripe = "#F4D35E" },
	@{ name = "bercedes_menz"; body = "#B8C4CC"; light = "#F2F5F7"; dark = "#313D47"; stripe = "#49B7D0" },
	@{ name = "borche"; body = "#F0C43A"; light = "#FFE985"; dark = "#60421B"; stripe = "#D43D35" },
	@{ name = "driftora"; body = "#9847C9"; light = "#D997F0"; dark = "#38214E"; stripe = "#55E4DF" },
	@{ name = "korvex"; body = "#3D6D4A"; light = "#89B870"; dark = "#172D27"; stripe = "#E7B94F" },
	@{ name = "nimbus"; body = "#318CC2"; light = "#8FE2F0"; dark = "#183D61"; stripe = "#F3F4ED" },
	@{ name = "pmv"; body = "#4A85C6"; light = "#A9D5F2"; dark = "#1C3156"; stripe = "#F3F4F5" },
	@{ name = "veldora"; body = "#C92D39"; light = "#FF7970"; dark = "#551D28"; stripe = "#F5D35E" },
	@{ name = "zephara"; body = "#D48228"; light = "#FFD178"; dark = "#60301F"; stripe = "#56CFCC" }
)

function New-Brush([string]$Color) {
	return New-Object System.Drawing.SolidBrush ([System.Drawing.ColorTranslator]::FromHtml($Color))
}

function Fill-Rect($Graphics, [string]$Color, [int]$X, [int]$Y, [int]$Width, [int]$Height) {
	$brush = New-Brush $Color
	try { $Graphics.FillRectangle($brush, $X, $Y, $Width, $Height) } finally { $brush.Dispose() }
}

function Draw-Atlas($Brand, [string]$Path) {
	$bitmap = New-Object System.Drawing.Bitmap 256, 256, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
	try {
		$graphics.Clear([System.Drawing.ColorTranslator]::FromHtml("#252B34"))
		$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::None

		Fill-Rect $graphics $Brand.dark 0 0 128 192
		Fill-Rect $graphics $Brand.body 2 2 124 58
		Fill-Rect $graphics $Brand.light 2 2 124 5
		Fill-Rect $graphics $Brand.dark 2 54 124 6
		Fill-Rect $graphics "#252B34" 0 65 128 4
		Fill-Rect $graphics $Brand.body 2 72 60 36
		Fill-Rect $graphics $Brand.light 2 72 60 4
		Fill-Rect $graphics $Brand.dark 2 104 60 4
		Fill-Rect $graphics $Brand.body 66 72 60 36
		Fill-Rect $graphics $Brand.light 66 72 60 4
		Fill-Rect $graphics $Brand.stripe 66 88 60 5
		Fill-Rect $graphics $Brand.dark 66 104 60 4
		Fill-Rect $graphics $Brand.body 2 112 124 44
		Fill-Rect $graphics $Brand.light 2 112 124 4
		Fill-Rect $graphics $Brand.stripe 2 127 124 5
		Fill-Rect $graphics $Brand.dark 2 150 124 6
		Fill-Rect $graphics "#E8EDF0" 12 137 32 3
		Fill-Rect $graphics "#E8EDF0" 82 137 32 3
		Fill-Rect $graphics "#F3D768" 2 160 28 28
		Fill-Rect $graphics $Brand.body 33 160 28 28
		Fill-Rect $graphics $Brand.dark 66 160 28 28
		Fill-Rect $graphics $Brand.stripe 98 160 28 28

		Fill-Rect $graphics "#102633" 128 0 128 48
		Fill-Rect $graphics "#1B485A" 130 2 124 44
		Fill-Rect $graphics "#6FD0DA" 130 3 124 3
		Fill-Rect $graphics "#B7F2EC" 138 8 18 2
		Fill-Rect $graphics "#4BA6B9" 159 8 82 2
		Fill-Rect $graphics "#102633" 130 39 124 5

		Fill-Rect $graphics "#171B20" 128 64 32 32
		for ($i = 0; $i -lt 32; $i += 6) {
			Fill-Rect $graphics "#252B32" (128 + $i) 64 2 32
		}
		Fill-Rect $graphics "#86939D" 160 64 24 24
		Fill-Rect $graphics "#D7E0E5" 162 66 20 20
		Fill-Rect $graphics "#364451" 164 68 16 16
		Fill-Rect $graphics "#AEBCC4" 166 70 12 12
		Fill-Rect $graphics "#27323D" 168 72 8 8
		Fill-Rect $graphics "#D6E0E5" 184 64 16 16
		Fill-Rect $graphics "#60717D" 186 66 12 12
		Fill-Rect $graphics "#E9EEF0" 189 69 6 6

		Fill-Rect $graphics "#242A32" 128 128 128 64
		Fill-Rect $graphics "#323B46" 130 130 124 14
		Fill-Rect $graphics "#131920" 130 148 124 42
		Fill-Rect $graphics "#B76A48" 130 148 124 3
		Fill-Rect $graphics "#53606A" 130 159 124 2
		Fill-Rect $graphics "#161B21" 160 128 28 16
		Fill-Rect $graphics "#D9483E" 164 130 20 12
		Fill-Rect $graphics "#FFCC65" 166 132 16 3
		Fill-Rect $graphics "#171D23" 192 128 30 16
		Fill-Rect $graphics "#ED4237" 196 130 22 12
		Fill-Rect $graphics "#F8D56A" 198 132 18 3
		Fill-Rect $graphics "#9AA7AF" 160 160 28 28
		Fill-Rect $graphics "#48545F" 162 162 24 24
		Fill-Rect $graphics "#AAB8C0" 164 164 20 20
		Fill-Rect $graphics "#52616C" 167 167 14 14
		Fill-Rect $graphics "#B4C0C8" 173 168 2 12
		Fill-Rect $graphics "#B4C0C8" 168 173 12 2
		Fill-Rect $graphics "#161A20" 192 160 32 32
		Fill-Rect $graphics "#687783" 194 162 28 28
		Fill-Rect $graphics "#303A44" 196 164 24 24
		Fill-Rect $graphics "#AEB9C0" 200 168 16 16

		Fill-Rect $graphics "#303840" 192 0 64 120
		Fill-Rect $graphics "#12171D" 194 2 60 20
		Fill-Rect $graphics "#BBC7CC" 196 5 56 4
		Fill-Rect $graphics "#9EAAB2" 194 32 60 4
		Fill-Rect $graphics "#586671" 194 40 60 8
		Fill-Rect $graphics "#DDE5E9" 194 56 60 4
		Fill-Rect $graphics "#76848D" 194 64 60 8
		Fill-Rect $graphics "#12171D" 194 80 60 16
		Fill-Rect $graphics "#AEBBC2" 194 84 60 3
		Fill-Rect $graphics "#454E58" 194 104 60 12

		Fill-Rect $graphics "#F5E68C" 224 128 30 12
		Fill-Rect $graphics "#FFFBE1" 225 129 28 8
		Fill-Rect $graphics "#9B2834" 224 144 30 12
		Fill-Rect $graphics "#F25056" 225 145 28 8
		Fill-Rect $graphics "#20242A" 192 192 64 32
		Fill-Rect $graphics "#ACB5BA" 194 194 60 5
		Fill-Rect $graphics "#68747E" 194 202 60 8
		Fill-Rect $graphics $Brand.stripe 194 214 60 6
		$bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
	} finally {
		$graphics.Dispose()
		$bitmap.Dispose()
	}
}

function Draw-Icon($Brand, [string]$Path) {
	$bitmap = New-Object System.Drawing.Bitmap 64, 64, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
	$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
	try {
		$graphics.Clear([System.Drawing.Color]::Transparent)
		$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
		Fill-Rect $graphics "#26323F" 8 48 48 4
		$bodyBrush = New-Brush $Brand.body
		$lightBrush = New-Brush $Brand.light
		$glassBrush = New-Brush "#50A9C0"
		$tireBrush = New-Brush "#15191D"
		$rimBrush = New-Brush "#B7C4CC"
		$stripeBrush = New-Brush $Brand.stripe
		try {
			$body = [System.Drawing.Point[]]@(
				[System.Drawing.Point]::new(5, 43), [System.Drawing.Point]::new(9, 35),
				[System.Drawing.Point]::new(17, 32), [System.Drawing.Point]::new(23, 21),
				[System.Drawing.Point]::new(42, 21), [System.Drawing.Point]::new(51, 33),
				[System.Drawing.Point]::new(58, 37), [System.Drawing.Point]::new(60, 45),
				[System.Drawing.Point]::new(56, 49), [System.Drawing.Point]::new(8, 49)
			)
			$graphics.FillPolygon($bodyBrush, $body)
			$graphics.FillPolygon($lightBrush, [System.Drawing.Point[]]@(
				[System.Drawing.Point]::new(23, 23), [System.Drawing.Point]::new(40, 23),
				[System.Drawing.Point]::new(47, 32), [System.Drawing.Point]::new(18, 32)
			))
			$graphics.FillPolygon($glassBrush, [System.Drawing.Point[]]@(
				[System.Drawing.Point]::new(25, 25), [System.Drawing.Point]::new(39, 25),
				[System.Drawing.Point]::new(44, 31), [System.Drawing.Point]::new(21, 31)
			))
			Fill-Rect $graphics $Brand.dark 10 39 43 3
			$graphics.FillRectangle($stripeBrush, 13, 35, 39, 2)
			foreach ($x in @(15, 46)) {
				$graphics.FillEllipse($tireBrush, $x - 6, 40, 13, 13)
				$graphics.FillEllipse($rimBrush, $x - 3, 43, 7, 7)
				$graphics.FillEllipse($tireBrush, $x - 1, 45, 3, 3)
			}
			Fill-Rect $graphics "#FFF4C0" 8 35 6 3
			Fill-Rect $graphics "#EF514A" 51 35 5 3
		} finally {
			$bodyBrush.Dispose()
			$lightBrush.Dispose()
			$glassBrush.Dispose()
			$tireBrush.Dispose()
			$rimBrush.Dispose()
			$stripeBrush.Dispose()
		}
		$bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
	} finally {
		$graphics.Dispose()
		$bitmap.Dispose()
	}
}

foreach ($brand in $brands) {
	$name = $brand.name
	Draw-Atlas $brand (Join-Path $root "src\main\resources\assets\racemod\textures\entity\car\$name.png")
	Draw-Icon $brand (Join-Path $root "src\main\resources\assets\racemod\textures\item\$name`_car.png")
}
